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
import java.util.Map;
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
    private final Map<OrderSagaStep, SagaStep> stepMap;

    /**
     * Saga 시작
     * - 첫 번째 Step 실행
     */
    @Transactional
    public void startSaga(UUID sagaId) {
        log.info("[SAGA][ORDER][SAGA_START][STARTED] sagaId={}", sagaId);

        OrderSaga saga = sagaService.findBySagaId(sagaId);

        // 멱등성 검증: 이미 종료된 Saga면 스킵
        if (saga.isTerminated()) {
            log.warn("[SAGA][ORDER][SAGA_START][SKIP_DUPLICATE] sagaId={} reason=ALREADY_TERMINATED", sagaId);
            return;
        }

        // 실행 가능한 Step 리스트
        List<OrderSagaStep> executableSteps = OrderSagaStep.getExecutableSteps();

        // 설정 오류(등록된 Step 없음): 주문이 이미 존재할 수 있어 상태를 정리
        if (executableSteps.isEmpty()) {
            String msg = "No SagaStep registered";
            log.error("[SAGA][ORDER][START][CONFIG_ERROR] sagaId={} msg={}", sagaId, msg);
            // Saga 실패 처리 + 주문 취소로 정리
            failSaga(sagaId, saga.getCurrentStep(), msg);
            return;
        }

        Order order = orderService.findOrderWithDetails(saga.getOrderId());
        // 첫 번째 Step 실행 (재고 예약)
        OrderSagaStep firstStepEnum = executableSteps.get(0);
        SagaStep firstStep = stepMap.get(firstStepEnum);

        // Step 실행 (이벤트만 발행하고 즉시 리턴)
        firstStep.execute(saga, order);

        // ====== 실행 결과 ======
        // 1. Kafka에 StockDecreaseEventReq 발행됨
        //    - Topic: order.stock.decrease.request
        //    - Key: sagaId
        //    - Value: {sagaId, orderId, productId, quantity, ...}
        // 2. OrderSaga 상태는 아직 STARTED
        //    - currentStep = STARTED (변경 안 됨)
        //    - 응답이 오면 그때 STOCK_RESERVED로 변경
        // 3. 이 메서드는 즉시 종료
        //    - 트랜잭션 커밋
        //    - Kafka Listener는 다음 메시지 대기
        log.info("[SAGA][ORDER][STEP][EXECUTED] sagaId={} orderId={} step={} stepCode={}",
                sagaId, saga.getOrderId(), firstStepEnum, firstStepEnum.getCode());
    }

    /**
     * Step 완료 후 다음 Step 진행
     */
    @Transactional
    public void continueAfterStep(UUID sagaId, OrderSagaStep completedStep) {
        log.info("[SAGA][ORDER][STEP][PROCEED] sagaId={} completedStep={}", sagaId, completedStep);

        OrderSaga saga = sagaService.findBySagaId(sagaId);

        // 멱등성: 이미 종료된 Saga
        if (saga.isTerminated()) {
            log.warn("[SAGA][ORDER][STEP][SKIP_DUPLICATE] sagaId={} reason=ALREADY_TERMINATED", sagaId);
            return;
        }

        // 실행 가능한 Step 리스트: [STOCK_RESERVED, COUPON_APPLIED, PAYMENT_CREATED]
        List<OrderSagaStep> steps = OrderSagaStep.getExecutableSteps();
        // 현재 Step의 인덱스: STARTED는 실행 목록에 없으므로 인덱스를 -1
        int currentIdx = (saga.getCurrentStep() == OrderSagaStep.STARTED)
                ? -1 : steps.indexOf(saga.getCurrentStep());

        // STARTED가 아닌데 리스트에 없으면 이상 상태
        if (currentIdx == -1 && saga.getCurrentStep() != OrderSagaStep.STARTED) {
            failSaga(sagaId, saga.getCurrentStep(), "현재 Step이 실행 목록에 없습니다.");
            return;
        }

        // Out-of-order 검증: 현재 Step 다음이 completedStep이어야 함
        int expectedIdx = currentIdx + 1;
        if (expectedIdx >= steps.size() || steps.get(expectedIdx) != completedStep) {
            log.warn("[SAGA][ORDER][STEP][IGNORE_OUT_OF_ORDER] sagaId={} completedStep={} expectedStep={} currentStep={}",
                    sagaId, completedStep,
                    expectedIdx < steps.size() ? steps.get(expectedIdx) : null,
                    saga.getCurrentStep());
            return;
        }

        // Saga 상태 전이 (검증 포함)
        saga.proceedToNextStep(completedStep);
        sagaService.save(saga);

        log.info("[SAGA][ORDER][STEP][COMPLETED] sagaId={} orderId={} step={} newStep={}",
                sagaId, saga.getOrderId(), completedStep, saga.getCurrentStep());

        // 다음 Step 실행
        int nextIdx = expectedIdx + 1;
        if (nextIdx >= steps.size()) {
            completeSaga(saga);
            return;
        }

        OrderSagaStep nextStepEnum = steps.get(nextIdx);
        SagaStep nextStep = stepMap.get(nextStepEnum);

        // 다음 Step 실행
        Order order = orderService.findOrderWithDetails(saga.getOrderId());
        nextStep.execute(saga, order);

        log.info("[SAGA][ORDER][STEP][EXECUTED] sagaId={} orderId={} step={} stepIndex={}",
                sagaId, saga.getOrderId(), nextStep.getName(), nextIdx);
    }

    /**
     * Saga 완료
     */
    private void completeSaga(OrderSaga saga) {
        saga.complete();
        sagaService.save(saga);

        log.info("[SAGA][ORDER][SAGA][COMPLETED] sagaId={} orderId={}",
                saga.getSagaId(), saga.getOrderId());
    }

    /**
     * Saga 실패 처리
     */
    @Transactional
    public void failSaga(UUID sagaId, OrderSagaStep failedStep, String errorMessage) {
        log.warn("[SAGA][ORDER][SAGA][FAILED] sagaId={} failedStep={} reason=BUSINESS_FAILURE msg={}",
                sagaId, failedStep, errorMessage);

        OrderSaga saga = sagaService.findBySagaId(sagaId);

        // 멱등성: 이미 종료된 Saga
        if (saga.isTerminated()) {
            log.warn("[SAGA][ORDER][SAGA][SKIP_DUPLICATE] sagaId={} reason=ALREADY_TERMINATED", sagaId);
            return;
        }

        Order order = orderService.findOrderWithDetails(saga.getOrderId());

        // 보상 시작
        saga.startCompensation();
        sagaService.save(saga);

        log.warn("[SAGA][ORDER][COMPENSATE][START] sagaId={} orderId={} failedStep={}",
                sagaId, saga.getOrderId(), failedStep);

        // 보상 실행
        compensate(saga, order);

        // 주문 취소
        orderService.cancelOrderBySaga(order.getOrderId(), errorMessage);
        log.info("[SAGA][ORDER_CANCELLED] sagaId={} orderId={}", sagaId, order.getOrderId());

        // Saga 실패 확정
        saga.fail(errorMessage);
        sagaService.save(saga);

        log.warn("[SAGA][ORDER][SAGA][FAILED_FINAL] sagaId={} orderId={} msg={}",
                sagaId, saga.getOrderId(), errorMessage);
    }

    /**
     * 보상 트랜잭션 실행
     * - 현재 Step까지 역순으로 보상 (Best Effort)
     */
    private void compensate(OrderSaga saga, Order order) {
        // 실행 가능한 Step 리스트
        List<OrderSagaStep> executableSteps = OrderSagaStep.getExecutableSteps();
        // 현재 Step의 인덱스 (마지막으로 성공한 Step)
        int lastSuccessIdx = executableSteps.indexOf(saga.getCurrentStep());

        if (lastSuccessIdx == -1) {
            log.info("[SAGA][ORDER][COMPENSATE][SKIP] sagaId={} currentStep={} reason=STEP_NOT_IN_EXECUTABLE_LIST",
                    saga.getSagaId(), saga.getCurrentStep());
            return;
        }

        // 역순 보상 (lastSuccessIdx → 0)
        for (int i = lastSuccessIdx; i >= 0; i--) {
            OrderSagaStep stepEnum = executableSteps.get(i);
            SagaStep step = stepMap.get(stepEnum);

            try {
                step.compensate(saga, order);
                log.info("[SAGA][ORDER][COMPENSATE][DONE] sagaId={} orderId={} step={} stepIndex={}",
                        saga.getSagaId(), saga.getOrderId(), stepEnum, i);

            } catch (Exception e) {
                log.error("[SAGA][ORDER][COMPENSATE][ERROR] sagaId={} orderId={} step={} stepIndex={} reason={}",
                        saga.getSagaId(), saga.getOrderId(), stepEnum, i, e.getClass().getSimpleName(), e);
                // Best Effort - 계속 진행
            }
        }

        log.warn("[SAGA][ORDER][COMPENSATE][END] sagaId={} orderId={}",
                saga.getSagaId(), saga.getOrderId());
    }
}