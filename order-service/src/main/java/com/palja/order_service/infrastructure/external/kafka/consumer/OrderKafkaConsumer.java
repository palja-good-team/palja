package com.palja.order_service.infrastructure.external.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.order_service.application.event.dto.response.*;
import com.palja.order_service.application.saga.OrderSagaOrchestrator;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.infrastructure.external.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Saga Kafka Listeners
 * - Saga Step 응답 이벤트 수신
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderSagaOrchestrator orchestrator;
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    // ====== payment event consumer  ======
    private static final String TYPE_PAYMENT_APPROVED = "PAYMENT_APPROVED";
    private static final String TYPE_PAYMENT_CANCELED = "PAYMENT_CANCELED";
    private static final int PAYLOAD_PREVIEW_MAX_LEN = 300;

    /**
     * 재고 차감 성공 응답
     * Topic: order.stock.decrease.success
     */
    @KafkaListener(topics = KafkaTopics.STOCK_DECREASE_SUCCESS)
    public void onStockDecreaseSuccess(SagaStepEventRes event) {
        log.info("Kafka 수신 (consumed): topic={} sagaId={} orderId={} step={}",
                KafkaTopics.STOCK_DECREASE_SUCCESS, event.getSagaId(), event.getOrderId(), OrderSagaStep.STOCK_DECREASED);

        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.STOCK_DECREASED);
    }

    /**
     * 재고 차감 실패 응답
     * Topic: order.stock.decrease.failure
     */
    @KafkaListener(topics = KafkaTopics.STOCK_DECREASE_FAILURE)
    public void onStockDecreaseFailure(SagaStepEventRes event) {
        log.info("Kafka 수신 (consumed): topic={} sagaId={} orderId={} step={} result=FAIL",
                KafkaTopics.STOCK_DECREASE_FAILURE, event.getSagaId(), event.getOrderId(), OrderSagaStep.STOCK_DECREASED);

        // 실패 처리 로그는 orchestrator.failSaga()가 남김
        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.STOCK_DECREASED, "재고 차감 실패");
    }

    /**
     * 쿠폰 사용 성공 응답
     * Topic: order.coupon.use.success
     */
    @KafkaListener(topics = KafkaTopics.COUPON_USE_SUCCESS)
    public void onCouponUseSuccess(SagaStepEventRes event) {
        log.info("Kafka 수신 (consumed): topic={} sagaId={} orderId={} step={}",
                KafkaTopics.COUPON_USE_SUCCESS, event.getSagaId(), event.getOrderId(), OrderSagaStep.COUPON_USED);

        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.COUPON_USED);
    }

    /**
     * 쿠폰 사용 실패 응답
     * Topic: order.coupon.use.failure
     */
    @KafkaListener(topics = KafkaTopics.COUPON_USE_FAILURE)
    public void onCouponUseFailure(SagaStepEventRes event) {
        log.info("Kafka 수신 (consumed): topic={} sagaId={} orderId={} step={} result=FAIL",
                KafkaTopics.COUPON_USE_FAILURE, event.getSagaId(), event.getOrderId(), OrderSagaStep.COUPON_USED);

        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.COUPON_USED, "쿠폰 사용 실패");
    }

    /**
     * 결제 생성 성공 응답
     * Topic: order.payment.create.success
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_CREATE_SUCCESS)
    public void onPaymentCreateSuccess(PaymentCreateEventRes event) {
        log.info("Kafka 수신 (consumed): topic={} sagaId={} orderId={} step={} paymentId={}",
                KafkaTopics.PAYMENT_CREATE_SUCCESS, event.getSagaId(), event.getOrderId(), OrderSagaStep.PAYMENT_CREATED, event.getPaymentId());

        // 결제 ID 반영은 사가 진행에 필요한 최소 side-effect라 Consumer에서 처리
        orderService.registerPayment(event.getOrderId(), event.getPaymentId());
        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.PAYMENT_CREATED);
    }

    /**
     * 결제 생성 실패 응답
     * Topic: order.payment.create.failure
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_CREATE_FAILURE)
    public void onPaymentCreateFailure(PaymentCreateEventRes event) {
        log.info("Kafka 수신 (consumed): topic={} sagaId={} orderId={} step={} result=FAIL",
                KafkaTopics.PAYMENT_CREATE_FAILURE, event.getSagaId(), event.getOrderId(), OrderSagaStep.PAYMENT_CREATED);

        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.PAYMENT_CREATED, "결제 생성 실패");
    }

    /**
     * 결제 승인 성공
     * topic: payment.order.approve.success
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_ORDER_APPROVE_SUCCESS)
    public void onPaymentApproveSuccess(PaymentBaseEventRes event) {
        log.info("Kafka 수신 (consumed): topic={} type={} eventId={} orderId={} paymentId={}",
                KafkaTopics.PAYMENT_ORDER_APPROVE_SUCCESS, event.getType(), event.getEventId(), event.getOrderId(), event.getPaymentId());

        if (!TYPE_PAYMENT_APPROVED.equals(event.getType())) {
            log.warn("Kafka 수신 무시 (ignored): topic={} reason=UNEXPECTED_TYPE type={} eventId={}",
                    KafkaTopics.PAYMENT_ORDER_APPROVE_SUCCESS, event.getType(), event.getEventId());
            return;
        }

        PaymentApproveEventRes payload = readPayload(event, PaymentApproveEventRes.class);

        // 주문 결제완료 반영 (CREATED -> PAID, paymentId 세팅)
        orderService.completeOrderPayment(payload.toCommand());
    }

    /**
     * 결제 취소 성공
     * topic: payment.order.cancel.success
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_ORDER_CANCEL_SUCCESS)
    public void onPaymentCancelSuccess(PaymentBaseEventRes event) {
        log.info("Kafka 수신 (consumed): topic={} type={} eventId={} orderId={} paymentId={}",
                KafkaTopics.PAYMENT_ORDER_CANCEL_SUCCESS, event.getType(), event.getEventId(), event.getOrderId(), event.getPaymentId());

        if (!TYPE_PAYMENT_CANCELED.equals(event.getType())) {
            log.warn("Kafka 수신 무시 (ignored): topic={} reason=UNEXPECTED_TYPE type={} eventId={}",
                    KafkaTopics.PAYMENT_ORDER_CANCEL_SUCCESS, event.getType(), event.getEventId());
            return;
        }

        PaymentCancelEventRes payload = readPayload(event, PaymentCancelEventRes.class);

        // 주문 취소 반영 (PAID -> CANCELED)
        orderService.cancelOrder(payload.toCommand());
    }

    // ===== payment 공통 파싱 유틸 =====
    private <T> T readPayload(PaymentBaseEventRes event, Class<T> payloadClass) {
        try {
            return objectMapper.readValue(event.getPayloadJson(), payloadClass);
        } catch (JsonProcessingException e) {
            String payloadPreview = preview(event.getPayloadJson(), PAYLOAD_PREVIEW_MAX_LEN);

            log.error("결제 이벤트 payload 역직렬화 실패 (payload deserialize failed):" +
                            " type={} eventId={} payloadClass={} payloadPreview={} errorType={}",
                    event.getType(), event.getEventId(), payloadClass.getSimpleName(), payloadPreview,
                    e.getClass().getSimpleName(), e);

            throw new IllegalStateException(event.getType() + " payloadJson deserialize failed", e);
        }
    }

    private static String preview(String raw, int maxLen) {
        if (raw == null) return "N/A";
        if (raw.length() <= maxLen) return raw;
        return raw.substring(0, maxLen) + "...(truncated)";
    }
}