package com.palja.order_service.application.saga;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.service.OrderSagaService;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.domain.repository.OrderSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCreationSagaOrchestrator {

    // 총 시도 횟수
    private static final int MAX_ATTEMPTS = 2;
    private static final long BACKOFF_MS = 200;

    private final OrderService orderService;
    private final OrderSagaService orderSagaService;
    private final OrderSagaRepository orderSagaRepository;
    private final List<SagaStep> sagaSteps;

    /**
     * 주문 생성 Saga 실행
     *
     * [처리 흐름]
     * 1. Order 조회
     * 2. Saga 조회/생성 (멱등성 보장)
     * 3. 각 Step 순차 실행
     * 4. 실패 시 보상 트랜잭션
     */
    @Transactional
    public void run(UUID orderId) {
        // Order 조회 (Step execute/compensate에서 상세 정보 필요지)
        Order order = orderService.findOrderWithDetails(orderId);

        // Saga 조회/생성 (멱등성 보장)
        OrderSaga saga = orderSagaService.findByOrderId(orderId);

        // 이미 종료된 Saga면 스킵
        if (saga.isTerminal()) {
            log.info("[SAGA][SKIP] orderId={}, sagaStatus={}, sagaStep={}",
                    orderId, saga.getSagaStatus(), saga.getSagaStep());
            return;
        }

        log.info("[SAGA][START] orderId={}, sagaId={}, sagaStep={}",
                orderId, saga.getSagaId(), saga.getSagaStep());

        try {
            // 각 Step 순차 실행
            for (SagaStep step : sagaSteps) {
                // 최신 Saga 상태 조회 (멱등성 판단용)
                saga = orderSagaService.reload(orderId);

                // 이미 완료된 Step은 스킵
                if (isStepAlreadyDone(saga, step)) {
                    log.info("[SAGA][{}][SKIP] orderId={}, alreadyDone={}, currentStep={}",
                            step.name(), orderId, step.successStep(), saga.getSagaStep());
                    continue;
                }

                log.info("[SAGA][STEP-START] orderId={}, step={}, currentSagaStep={}",
                        orderId, step.name(), saga.getSagaStep());

                // Step 실행 (재시도 포함)
                executeStepWithRetry(step, order);

                log.info("[SAGA][STEP-DONE] orderId={}, step={}, successStep={}",
                        orderId, step.name(), step.successStep());
            }

            // 모든 Step 성공 → Saga 완료
            saga = orderSagaService.reload(orderId);
            saga.complete();
            orderSagaRepository.save(saga);

            log.info("[SAGA][COMPLETED] orderId={}, sagaId={}", orderId, saga.getSagaId());

        } catch (BusinessException e) {
            // 비즈니스 실패 → 보상 + FAIL
            handleSagaFailure(order, orderId, e);

        } catch (Exception e) {
            // 시스템 실패 → 보상 + FAIL
            handleSagaError(order, orderId, e);
        }
    }

    /**
     * Step 실행 (재시도 포함)
     *
     * [재시도 정책]
     * - 최대 MAX_ATTEMPTS 시도
     * - 낙관적 락 충돌 시: 재조회 후 완료 여부 확인
     * - 비즈니스 예외: 재시도 안 함
     * - 시스템 예외: 재시도 함
     */
    private void executeStepWithRetry(SagaStep step, Order order) {
        UUID orderId = order.getOrderId();

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                // 매 시도마다 최신 Saga 조회
                OrderSaga saga = orderSagaService.reload(orderId);

                // 종료 상태면 스킵
                if (saga.isTerminal()) {
                    log.info("[SAGA][{}][SKIP] orderId={}, sagaTerminal={}",
                            step.name(), orderId, saga.getSagaStatus());
                    return;
                }

                // 이미 완료면 스킵 (동시 실행 방지)
                if (isStepAlreadyDone(saga, step)) {
                    log.info("[SAGA][{}][SKIP] orderId={}, alreadyDone={}",
                            step.name(), orderId, step.successStep());
                    return;
                }

                log.info("[SAGA][{}][ATTEMPT] orderId={}, attempt={}",
                        step.name(), orderId, attempt);

                // 외부 시스템 호출
                step.execute(order);

                // Saga 상태 업데이트
                saga.markStep(step.successStep());
                orderSagaRepository.saveAndFlush(saga);

                return; // 성공

            } catch (ObjectOptimisticLockingFailureException e) {
                // 낙관적 락 충돌 → 재조회 후 완료 여부 확인
                handleOptimisticLockConflict(step, orderId, attempt);
            } catch (Exception e) {
                // 시스템 예외 → 재시도
                if (attempt == MAX_ATTEMPTS) {
                    throw e;
                }
                backoff(attempt);
            }
        }
    }

    /**
     * 낙관적 락 충돌 처리
     */
    private void handleOptimisticLockConflict(SagaStep step, UUID orderId, int attempt) {
        log.warn("[SAGA][{}][OPTIMISTIC_LOCK] orderId={}, attempt={}",
                step.name(), orderId, attempt);

        // 최신 Saga 조회
        OrderSaga latest = orderSagaService.reload(orderId);

        // 이미 완료됐으면 종료
        if (isStepAlreadyDone(latest, step)) {
            log.info("[SAGA][{}][CONCURRENT_DONE] orderId={}, sagaStep={}",
                    step.name(), orderId, latest.getSagaStep());
            return;
        }

        // 아직 완료 안 됐으면 재시도
        if (attempt == MAX_ATTEMPTS) {
            throw new IllegalStateException(
                    String.format("Max retry exceeded for step %s", step.name())
            );
        }
        backoff(attempt);
    }

    /**
     * Saga 실패 처리 (비즈니스 예외)
     */
    private void handleSagaFailure(Order order, UUID orderId, BusinessException e) {
        OrderSaga saga = orderSagaService.safeReloadForFail(orderId);

        log.error("[SAGA][FAIL] orderId={}, sagaStep={}, errorCode={}, msg={}",
                orderId, saga.getSagaStep(), e.getErrorCode(), e.getMessage(), e);

        // 보상 트랜잭션
        compensate(order, saga);

        // FAIL 마킹
        saga.fail(e.getMessage());
        orderSagaRepository.save(saga);
    }

    /**
     * Saga 실패 처리 (시스템 예외)
     */
    private void handleSagaError(Order order, UUID orderId, Exception e) {
        OrderSaga saga = orderSagaService.safeReloadForFail(orderId);

        log.error("[SAGA][ERROR] orderId={}, sagaStep={}, msg={}",
                orderId, saga.getSagaStep(), e.getMessage(), e);

        // 보상 트랜잭션
        compensate(order, saga);

        // FAIL 마킹
        saga.fail("SYSTEM_ERROR: " + e.getMessage());
        orderSagaRepository.save(saga);
    }

    /**
     * 보상 트랜잭션
     * - 성공한 Step만 역순으로 보상
     * - Best Effort (실패해도 계속 진행)
     */
    private void compensate(Order order, OrderSaga saga) {
        log.warn("[SAGA][COMPENSATE-START] orderId={}, sagaStep={}",
                order.getOrderId(), saga.getSagaStep());

        for (int i = sagaSteps.size() - 1; i >= 0; i--) {
            SagaStep step = sagaSteps.get(i);

            // 성공한 Step만 보상
            if (!isStepAlreadyDone(saga, step)) {
                continue;
            }

            try {
                step.compensate(order);
            } catch (Exception e) {
                // Best Effort: 보상 실패해도 계속 진행
                log.error("[SAGA][COMPENSATE-FAIL] step={}, orderId={}, msg={}",
                        step.name(), order.getOrderId(), e.getMessage(), e);
            }
        }

        log.warn("[SAGA][COMPENSATE-END] orderId={}", order.getOrderId());
    }

    // ===== Private =====
    /**
     * Step이 이미 완료됐는지 확인
     */
    private boolean isStepAlreadyDone(OrderSaga saga, SagaStep step) {
        return saga.getSagaStep().isAtLeast(step.successStep());
    }

    /**
     * 재시도 대기 (Exponential Backoff)
     */
    private void backoff(int attempt) {
        try {
            Thread.sleep(BACKOFF_MS * attempt);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}