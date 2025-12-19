package com.palja.order_service.application.saga.steps;

import com.palja.order_service.application.dto.event.request.PaymentCancelEventReq;
import com.palja.order_service.application.dto.event.request.PaymentCreateEventReq;
import com.palja.order_service.application.port.kafka.OrderEventPublisher;
import com.palja.order_service.application.saga.SagaStep;
import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Step 3: 결제 생성
 *
 * 정방향: 결제 생성 요청 이벤트 발행
 * 보상: 결제 취소 요청 이벤트 발행
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
    public void execute(OrderSaga saga, Order order) {

        log.info("[SAGA][ORDER][PAYMENT_CREATE][READY] sagaId={} orderId={} amount={}",
                saga.getSagaId(),
                order.getOrderId(),
                order.getOrderAmount().getFinalAmount());

        // 결제 생성 요청 이벤트 발행
        PaymentCreateEventReq event = PaymentCreateEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                order.getUserId(),
                order.getOrderAmount().getFinalAmount(),
                order.getStatus().name()
        );

        try {
            // Kafka 발행: Kafka Producer가 메시지 전송
            eventPublisher.publishPaymentCreate(event);

            // Producer에서 Kafka 발행 로그 있음
            // 여기서는 Saga 단계 기준으로 발행 완료만 표시
            log.info("[SAGA][ORDER][PAYMENT_CREATE][PUBLISHED] sagaId={} orderId={}",
                    saga.getSagaId(), order.getOrderId());

        } catch (Exception e) {
            // Kafka 전송 실패 (네트워크 오류 등)
            log.error("[SAGA][ORDER][PAYMENT_CREATE][FAILED] sagaId={} orderId={} reason={}",
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

        log.warn("[SAGA][ORDER][PAYMENT_CANCEL][START] sagaId={} orderId={} paymentId={}",
                saga.getSagaId(), order.getOrderId(), paymentId);

        // 결제 취소 요청 이벤트 발행
        PaymentCancelEventReq event = PaymentCancelEventReq.of(
                saga.getSagaId(),
                order.getOrderId(),
                paymentId,
                order.getOrderAmount().getFinalAmount(),
                "SAGA_ROLLBACK: 주문 생성 실패"
        );

        try {
            eventPublisher.publishPaymentCancel(event);

            log.info("[SAGA][ORDER][PAYMENT_CANCEL][PUBLISHED] sagaId={} orderId={}",
                    saga.getSagaId(), order.getOrderId());

        } catch (Exception e) {
            // 보상은 Best Effort
            log.error("[SAGA][ORDER][PAYMENT_CANCEL][FAILED] sagaId={} orderId={} reason={}",
                    saga.getSagaId(), order.getOrderId(), e.getClass().getSimpleName(), e);
        }
    }
}