package com.palja.order_service.application.saga;

import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.application.service.OrderSagaService;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Order Saga Orchestrator
 *
 * 역할:
 * - Saga의 생명주기 관리
 * - Step 실행 및 보상 조율
 * - sagaId 중심으로 동작
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderSagaOrchestrator {

    private final OrderService orderService;
    private final OrderSagaService sagaService;
    private final List<com.palja.order_service.application.saga.SagaStep> steps;

    /**
     * Saga 시작
     * - 첫 번째 Step 실행
     */
    @Transactional
    public void startSaga(UUID sagaId) {
        log.info("[SAGA][START] sagaId={}", sagaId);

        OrderSaga saga = sagaService.findBySagaId(sagaId);

        // 멱등성 검증: 이미 종료된 Saga면 스킵
        if (saga.isTerminated()) {
            log.warn("[SAGA][ALREADY_TERMINATED] sagaId={}", sagaId);
            return;
        }

        log.debug("[SAGA][STATE] sagaId={}, orderId={}, status={}, currentStep={}",
                sagaId, saga.getOrderId(), saga.getStatus(), saga.getCurrentStep());

        Order order = orderService.findOrderWithDetails(saga.getOrderId());

        // 첫 번째 Step 실행 (재고 예약)
        // Spring의 @Order 어노테이션으로 정렬된 Step 리스트
        // steps = [ReserveStockStep(1), ApplyCouponStep(2), CreatePaymentStep(3)]
        SagaStep firstStep = steps.get(0);  // ReserveStockStep

        // Step 실행 (이벤트만 발행하고 즉시 리턴)
        firstStep.execute(saga, order);

        // ====== 실행 결과 ======
        // 1. Kafka에 StockDeductEventReq 발행됨
        //    - Topic: order.deduct.req
        //    - Key: sagaId
        //    - Value: {sagaId, orderId, productId, quantity, ...}
        // 2. OrderSaga 상태는 아직 STARTED
        //    - currentStep = STARTED (변경 안 됨)
        //    - 응답이 오면 그때 STOCK_RESERVED로 변경
        // 3. 이 메서드는 즉시 종료
        //    - 트랜잭션 커밋
        //    - Kafka Listener는 다음 메시지 대기
        log.info("[SAGA][STEP][EXECUTED] sagaId={}, step={}, stepIndex=0",
                sagaId, firstStep.getName());
    }

    /**
     * Step 완료 후 다음 Step 진행
     */
    @Transactional
    public void continueAfterStep(UUID sagaId, OrderSagaStep completedStep) {
        log.info("[SAGA][PROCEED] sagaId={}, completedStep={}", sagaId, completedStep);

        OrderSaga saga = sagaService.findBySagaId(sagaId);

        if (saga.isTerminated()) {
            log.warn("[SAGA][ALREADY_TERMINATED] sagaId={}", sagaId);
            return;
        }

        // Saga 상태 업데이트
        saga.proceedToNextStep(completedStep);
        sagaService.save(saga);

        log.info("[SAGA][STEP][COMPLETED] sagaId={}, step={}, newStep={}",
                sagaId, completedStep, saga.getCurrentStep());


        Order order = orderService.findOrderWithDetails(saga.getOrderId());

        // 다음 Step 실행 (statuscode 로 변경)
        int completedStepIndex = getStepIndex(completedStep);
        int nextStepIndex = completedStepIndex + 1;

        if (nextStepIndex >= steps.size()) {
            // 모든 Step 완료 -> Saga 완료
            completeSaga(saga);
        } else {
            // 다음 Step 실행
            SagaStep nextStep = steps.get(nextStepIndex);
            nextStep.execute(saga, order);

            log.info("[SAGA][NEXT_STEP][EXECUTED] sagaId={}, step={}",
                    sagaId, nextStep.getName());
        }
    }

    /**
     * Step 스킵 처리
     */
    public void skipStep(UUID sagaId, OrderSagaStep skippedStep) {
        OrderSaga saga = sagaService.findBySagaId(sagaId);
        saga.proceedToNextStep(skippedStep);
        sagaService.save(saga);

        int nextStepIndex = getStepIndex(skippedStep) + 1;
        if (nextStepIndex < steps.size()) {
            steps.get(nextStepIndex).execute(saga,
                    orderService.findOrderWithDetails(saga.getOrderId()));
        } else {
            completeSaga(saga);
        }
    }

    /**
     * Saga 완료
     */
    private void completeSaga(OrderSaga saga) {
        saga.complete();
        sagaService.save(saga);

        log.info("[ORCHESTRATOR][COMPLETED] sagaId={}, orderId={}",
                saga.getSagaId(), saga.getOrderId());
    }

    /**
     * Saga 실패 처리
     */
    @Transactional
    public void failSaga(UUID sagaId, OrderSagaStep failedStep, String errorMessage) {
        log.error("[SAGA][FAILURE] sagaId={}, failedStep={}, error={}",
                sagaId, failedStep, errorMessage);

        OrderSaga saga = sagaService.findBySagaId(sagaId);

        if (saga.isTerminated()) {
            log.warn("[SAGA][ALREADY_TERMINATED] sagaId={}", sagaId);
            return;
        }

        log.warn("[SAGA][COMPENSATE][START] sagaId={}, failedStep={}", sagaId, failedStep);

        Order order = orderService.findOrderWithDetails(saga.getOrderId());

        // 보상 트랜잭션 시작
        saga.startCompensation();
        sagaService.save(saga);

        compensate(saga, order, failedStep);

        // Saga 실패 처리
        saga.fail(errorMessage);
        sagaService.save(saga);

        log.error("[SAGA][FAIL] sagaId={}, reason={}", sagaId, errorMessage);
    }

    /**
     * 보상 트랜잭션 실행
     * - 실패한 Step 이전까지 역순으로 보상
     */
    private void compensate(OrderSaga saga, Order order, OrderSagaStep failedStep) {
        log.warn("[SAGA][COMPENSATE][START] sagaId={}, failedStep={}",
                saga.getSagaId(), failedStep);

        int failedStepIndex = getStepIndex(failedStep);

        // 실패한 Step 이전까지 역순으로 보상
        for (int i = failedStepIndex - 1; i >= 0; i--) {
            SagaStep step = steps.get(i);

            try {
                step.compensate(saga, order);
                log.info("[SAGA][COMPENSATE][DONE] sagaId={}, step={}",
                        saga.getSagaId(), step.getName());
            } catch (Exception e) {
                log.error("[SAGA][COMPENSATE][ERROR] sagaId={}, step={}, error={}",
                        saga.getSagaId(), step.getName(), e.getMessage(), e);
                // Best Effort - 계속 진행
            }
        }

        log.warn("[SAGA][COMPENSATE][END] sagaId={}", saga.getSagaId());
    }

    /**
     * SagaStep enum을 Step 인덱스로 변환
     */
    private int getStepIndex(OrderSagaStep orderSagaStep) {
        return switch (orderSagaStep) {
            case STOCK_RESERVED -> 0;
            case COUPON_APPLIED -> 1;
            case PAYMENT_CREATED -> 2;
            default -> throw new IllegalArgumentException("Invalid step: " + orderSagaStep);
        };
    }
}