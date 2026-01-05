package com.palja.order_service.application.saga.steps;

import com.palja.order_service.application.event.dto.request.PaymentCancelEventReq;
import com.palja.order_service.application.event.dto.request.PaymentCreateEventReq;
import com.palja.order_service.application.port.OrderEventPublisher;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Step 3: 결제 생성
 *
 * 정방향: 결제 생성 요청 이벤트 발행
 * 보상: 결제 취소 요청 이벤트 발행 (Best Effort)
 */
@Slf4j
@org.springframework.core.annotation.Order(3)
@Component
@RequiredArgsConstructor
public class CreatePaymentStep implements SagaStep {

    private final OrderEventPublisher eventPublisher;

    @Override
    public String getName() {
        return "결제생성";
    }

    @Override
    public OrderSagaStep getStepType() {
        return OrderSagaStep.PAYMENT_CREATED;
    }

    @Override
    public void execute(OrderSaga saga, Order order) {
        // 결제 생성 요청 이벤트 발행
        PaymentCreateEventReq event = PaymentCreateEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                order.getUserId(),
                order.getOrderAmount().getFinalAmount(),
                order.getStatus().name()
        );

        log.info("결제 생성 요청 발행 (payment create requested): sagaId={} orderId={} userId={} amount={} orderStatus={}",
                saga.getSagaId(), order.getOrderId(), order.getUserId(),
                order.getOrderAmount().getFinalAmount(),order.getStatus().name());

        try {
            // Kafka 발행
            eventPublisher.publishPaymentCreate(event);
        } catch (Exception e) {
            // 발행 실패는 ERROR (사가 실패)
            log.error("결제 생성 요청 발행 실패 (payment create publish failed): sagaId={} orderId={} errorType={}",
                    saga.getSagaId(), order.getOrderId(), e.getClass().getSimpleName(), e);
            throw e;
        }
    }

    @Override
    public void compensate(OrderSaga saga, Order order) {
        UUID paymentId = order.getPaymentId();

        // 결제가 생성되지 않았다면 보상도 없음
        if (paymentId == null) {
            return;
        }

        // 결제 취소 요청 이벤트 발행
        PaymentCancelEventReq event = PaymentCancelEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                paymentId,
                order.getOrderAmount().getFinalAmount(),
                "SAGA_ROLLBACK: 주문 생성 실패"
        );

        log.warn("결제 취소 보상 시작 (payment cancel compensation started): sagaId={} orderId={} paymentId={} amount={}",
                saga.getSagaId(), order.getOrderId(), paymentId, order.getOrderAmount().getFinalAmount());

        try {
            // Kafka 발행
            eventPublisher.publishPaymentCancel(event);

            log.info("결제 취소 요청 발행 (payment cancel requested): sagaId={} orderId={} paymentId={} amount={}",
                    saga.getSagaId(), order.getOrderId(), paymentId, order.getOrderAmount().getFinalAmount());

        } catch (Exception e) {
            // 보상 실패는 ERROR, Best Effort
            log.error("결제 취소 요청 발행 실패 (payment cancel publish failed): sagaId={} orderId={} paymentId={} errorType={}",
                    saga.getSagaId(), order.getOrderId(), paymentId, e.getClass().getSimpleName(), e);
        }
    }
}