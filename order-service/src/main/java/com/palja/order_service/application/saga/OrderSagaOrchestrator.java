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
     * Saga 시작 - 첫 번째 Step 실행 요청 발행
     */
    @Transactional
    public void startSaga(UUID sagaId) {
        OrderSaga saga = sagaService.findBySagaId(sagaId);
        UUID orderId = saga.getOrderId();

        log.info("Saga 시작 요청 (saga start requested): sagaId={} orderId={} currentStep={}",
                sagaId, orderId, saga.getCurrentStep());

        // 멱등성: 이미 종료된 Saga면 스킵 (중복 메시지/재처리 케이스)
        if (saga.isTerminated()) {
            log.warn("Saga 시작 무시 (ignore): sagaId={} orderId={} reason=ALREADY_TERMINATED currentStep={}",
                    sagaId, orderId, saga.getCurrentStep());
            return;
        }

        // 실행 가능한 Step 리스트
        List<OrderSagaStep> steps = OrderSagaStep.getExecutableSteps();
        // 설정 오류 (등록된 Step 없음)
        if (steps.isEmpty()) {
            failSaga(sagaId, saga.getCurrentStep(), "CONFIG_ERROR: executable steps is empty");
            return;
        }

        Order order = orderService.findOrderWithDetails(orderId);

        // 첫 번째 Step 실행 (재고차감)
        OrderSagaStep firstStepEnum = steps.get(0);
        SagaStep firstStep = stepMap.get(firstStepEnum);
        if (firstStep == null) {
            failSaga(sagaId, saga.getCurrentStep(), "CONFIG_ERROR: step bean not found. step=" + firstStepEnum);
            return;
        }

        // Step 실행 (이벤트 발행)
        firstStep.execute(saga, order);

        log.info("Saga Step 실행 요청 발행 (step execution requested): sagaId={} orderId={} step={}",
                sagaId, orderId, firstStepEnum);

        // ====== 실행 결과 ======
        // 1. Kafka에 StockDecreaseEventReq 발행됨
        //    - Topic: order.stock.decrease.request
        //    - Key: sagaId
        //    - Value: {sagaId, orderId, productId, quantity, ...}
        // 2. OrderSaga 상태는 아직 STARTED
        //    - currentStep = STARTED (변경 안 됨)
        //    - 응답이 오면 그때 STOCK_DECREASE로 변경
        // 3. 이 메서드는 즉시 종료
        //    - 트랜잭션 커밋
        //    - Kafka Listener는 다음 메시지 대기
    }

    /**
     * Step 완료 후 다음 Step 진행
     */
    @Transactional
    public void continueAfterStep(UUID sagaId, OrderSagaStep completedStep) {
        OrderSaga saga = sagaService.findBySagaId(sagaId);
        UUID orderId = saga.getOrderId();

        // 멱등성: 이미 종료된 Saga
        if (saga.isTerminated()) {
            log.warn("Saga 진행 무시 (ignore): sagaId={} orderId={} reason=ALREADY_TERMINATED currentStep={}",
                    sagaId, orderId, saga.getCurrentStep());
            return;
        }

        // 실행 가능한 Step 리스트
        List<OrderSagaStep> steps = OrderSagaStep.getExecutableSteps();

        // 현재 Step 인덱스 (STARTED는 -1)
        int currentIdx = (saga.getCurrentStep() == OrderSagaStep.STARTED)
                ? -1 : steps.indexOf(saga.getCurrentStep());

        if (currentIdx == -1 && saga.getCurrentStep() != OrderSagaStep.STARTED) {
            failSaga(sagaId, saga.getCurrentStep(), "INVALID_STATE: currentStep not in executable steps");
            return;
        }

        // out-of-order 방지: "현재 다음 step"만 인정
        int expectedIdx = currentIdx + 1;
        OrderSagaStep expectedStep = expectedIdx < steps.size() ? steps.get(expectedIdx) : null;
        if (expectedStep != completedStep) {
            log.warn("Saga Step 순서 불일치로 무시 (out-of-order ignored): sagaId={} orderId={} completedStep={} expectedStep={} currentStep={}",
                    sagaId, orderId, completedStep, expectedStep, saga.getCurrentStep());
            return;
        }

        // Saga Step 상태 전이: 완료된 SAGA 상태
        saga.proceedToNextStep(completedStep);
        sagaService.save(saga);

        log.info("Saga Step 완료 반영 (step completed): sagaId={} orderId={} completedStep={} newCurrentStep={}",
                sagaId, orderId, completedStep, saga.getCurrentStep());

        // 다음 Step 결정
        int nextIdx = expectedIdx + 1;
        if (nextIdx >= steps.size()) {
            completeSaga(saga);
            return;
        }

        OrderSagaStep nextStepEnum = steps.get(nextIdx);
        SagaStep nextStep = stepMap.get(nextStepEnum);
        if (nextStep == null) {
            failSaga(sagaId, saga.getCurrentStep(), "CONFIG_ERROR: step bean not found. step=" + nextStepEnum);
            return;
        }

        Order order = orderService.findOrderWithDetails(orderId);

        // 적용 대상 아니면 스킵, 다음 step으로 진행 (재귀)
        if (!nextStep.isApplicable(saga, order)) {
            log.info("Saga Step 스킵 (step skipped): sagaId={} orderId={} step={} reason=NOT_APPLICABLE",
                    sagaId, orderId, nextStepEnum);

            continueAfterStep(sagaId, nextStepEnum);
            return;
        }

        nextStep.execute(saga, order);

        log.info("Saga Step 실행 요청 발행 (step execution requested): sagaId={} orderId={} step={}",
                sagaId, orderId, nextStepEnum);
    }

    /**
     * Saga 완료
     */
    private void completeSaga(OrderSaga saga) {
        // Saga 및 Saga Step 상태 전이: 완료
        saga.complete();
        sagaService.save(saga);

        log.info("Saga 완료 (saga completed): sagaId={} orderId={}",
                saga.getSagaId(), saga.getOrderId());
    }

    /**
     * Saga 실패 처리
     */
    @Transactional
    public void failSaga(UUID sagaId, OrderSagaStep failedStep, String errorMessage) {
        OrderSaga saga = sagaService.findBySagaId(sagaId);
        UUID orderId = saga.getOrderId();

        // 멱등성: 이미 종료된 Saga
        if (saga.isTerminated()) {
            log.warn("Saga 실패 처리 무시 (ignore): sagaId={} orderId={} reason=ALREADY_TERMINATED currentStep={}",
                    sagaId, orderId, saga.getCurrentStep());
            return;
        }

        log.warn("Saga 실패 처리 시작 (saga failed): sagaId={} orderId={} failedStep={} msg={}",
                sagaId, orderId, failedStep, errorMessage);

        Order order = orderService.findOrderWithDetails(orderId);

        // Saga 상태 전이: 보상
        saga.startCompensation();
        sagaService.save(saga);

        log.warn("Saga 보상 시작 (compensation started): sagaId={} orderId={} fromStep={}",
                sagaId, orderId, saga.getCurrentStep());

        // 보상 시작
        compensate(saga, order);

        // 주문 취소
        orderService.cancelOrderBySaga(orderId, errorMessage);
        log.info("주문 취소 완료 (order cancelled): sagaId={} orderId={}", sagaId, orderId);

        // Saga 실패 확정 (Saga 상태 전이: 실패)
        saga.fail(errorMessage);
        sagaService.save(saga);

        log.warn("Saga 실패 확정 (saga failed finalized): sagaId={} orderId={} msg={}",
                sagaId, orderId, errorMessage);
    }

    /**
     * 보상 트랜잭션 실행 (Best Effort)
     */
    private void compensate(OrderSaga saga, Order order) {
        // 실행 가능한 Saga Step 리스트
        List<OrderSagaStep> executableSteps = OrderSagaStep.getExecutableSteps();
        // 현재 Saga Step의 인덱스: 마지막으로 성공한 Step
        int lastSuccessIdx = executableSteps.indexOf(saga.getCurrentStep());

        if (lastSuccessIdx == -1) {
            // 보상 대상 자체가 없으면 조용히 종료(필요 시 INFO 1줄)
            log.info("Saga 보상 스킵 (compensation skipped): sagaId={} orderId={} currentStep={} reason=STEP_NOT_IN_EXECUTABLE_LIST",
                    saga.getSagaId(), saga.getOrderId(), saga.getCurrentStep());
            return;
        }

        // 역순 보상 (lastSuccessIdx → 0)
        for (int i = lastSuccessIdx; i >= 0; i--) {
            OrderSagaStep stepEnum = executableSteps.get(i);
            SagaStep step = stepMap.get(stepEnum);

            if (step == null) {
                // 설정 오류지만 Best Effort: 다음 보상으로 진행
                log.error("Saga 보상 Step 누락 (missing step): sagaId={} orderId={} step={}",
                        saga.getSagaId(), saga.getOrderId(), stepEnum);
                continue;
            }

            try {
                step.compensate(saga, order);
                log.info("Saga 보상 완료 (compensated): sagaId={} orderId={} step={}",
                        saga.getSagaId(), saga.getOrderId(), stepEnum);

            } catch (Exception e) {
                log.error("Saga 보상 실패 (compensation failed): sagaId={} orderId={} step={} errorType={}",
                        saga.getSagaId(), saga.getOrderId(), stepEnum, e.getClass().getSimpleName(), e);
            }
        }

        log.warn("Saga 보상 종료 (compensation ended): sagaId={} orderId={}",
                saga.getSagaId(), saga.getOrderId());
    }
}