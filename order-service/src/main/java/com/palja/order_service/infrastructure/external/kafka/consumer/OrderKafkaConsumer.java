package com.palja.order_service.infrastructure.external.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.order_service.application.event.dto.request.SagaStartEventReq;
import com.palja.order_service.application.event.dto.response.*;
import com.palja.order_service.application.saga.OrderSagaOrchestrator;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.infrastructure.external.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Saga Kafka Listeners
 * - Saga 시작 이벤트 수신
 * - 각 Step 응답 이벤트 수신
 * - Orchestrator 호출
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final OrderSagaOrchestrator orchestrator;
    private final OrderService orderService;

    private final ObjectMapper objectMapper;

    /**
     * Saga 시작 이벤트 수신
     *  Topic: order.saga.start.request
     */
    @KafkaListener(topics = KafkaTopics.SAGA_START_REQUEST)
    public void onSagaStart(ConsumerRecord<String, Object> record) {

        SagaStartEventReq event = objectMapper.convertValue(record.value(), SagaStartEventReq.class);

        log.info("[KAFKA][ORDER][SAGA_START][CONSUMED] topic={} partition={} offset={} sagaId={} orderId={}",
                record.topic(), record.partition(), record.offset(),
                event.getSagaId(), event.getOrderId());

        orchestrator.startSaga(event.getSagaId());
    }

    /**
     * 재고 차감 성공 응답
     * Topic: order.stock.decrease.success
     */
    @KafkaListener(topics = KafkaTopics.STOCK_DECREASE_SUCCESS)
    public void onStockDecreaseSuccess(ConsumerRecord<String, Object> record) {

        SagaStepEventRes event = objectMapper.convertValue(record.value(), SagaStepEventRes.class);

        log.info("[KAFKA][ORDER][INVENTORY_DECREASE][CONSUMED] topic={} partition={} offset={} sagaId={} orderId={}",
                record.topic(), record.partition(), record.offset(),
                event.getSagaId(), event.getOrderId());

        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.STOCK_RESERVED);
    }

    /**
     * 재고 차감 실패 응답
     * Topic: order.stock.decrease.failure
     */
    @KafkaListener(topics = KafkaTopics.STOCK_DECREASE_FAILURE)
    public void onStockDecreaseFailure(ConsumerRecord<String, Object> record) {

        SagaStepEventRes event = objectMapper.convertValue(record.value(), SagaStepEventRes.class);

        log.info("[KAFKA][ORDER][INVENTORY_DECREASE][CONSUMED] topic={} partition={} offset={} sagaId={} orderId={}",
                record.topic(), record.partition(), record.offset(),
                event.getSagaId(), event.getOrderId());

        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.STOCK_RESERVED, "재고 차감 실패");

        // 비즈니스 실패 응답 처리 결과를 남기는 로그
        log.warn("[SAGA][ORDER][INVENTORY_DECREASE][FAILED] step={} sagaId={} orderId={} reason=BUSINESS_FAILURE",
                OrderSagaStep.STOCK_RESERVED, event.getSagaId(), event.getOrderId());
    }

    /**
     * 쿠폰 사용 성공 응답
     * Topic: order.coupon.use.success
     */
    @KafkaListener(topics = KafkaTopics.COUPON_USE_SUCCESS)
    public void onCouponUseSuccess(ConsumerRecord<String, Object> record) {

        SagaStepEventRes event = objectMapper.convertValue(record.value(), SagaStepEventRes.class);

        log.info("[KAFKA][ORDER][COUPON_USE][CONSUMED] topic={} partition={} offset={} sagaId={} orderId={}",
                record.topic(), record.partition(), record.offset(),
                event.getSagaId(), event.getOrderId());

        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.COUPON_APPLIED);
    }

    /**
     * 쿠폰 사용 실패 응답
     * Topic: order.coupon.use.failure
     */
    @KafkaListener(topics = KafkaTopics.COUPON_USE_FAILURE)
    public void onCouponUseFailure(ConsumerRecord<String, Object> record) {

        SagaStepEventRes event = objectMapper.convertValue(record.value(), SagaStepEventRes.class);

        log.info("[KAFKA][ORDER][COUPON_USE][CONSUMED] topic={} partition={} offset={} sagaId={} orderId={}",
                record.topic(), record.partition(), record.offset(),
                event.getSagaId(), event.getOrderId());

        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.COUPON_APPLIED, "쿠폰 사용 실패");

        log.warn("[SAGA][ORDER][COUPON_USE][FAILED] step={} sagaId={} orderId={} reason=BUSINESS_FAILURE",
                OrderSagaStep.COUPON_APPLIED, event.getSagaId(), event.getOrderId());
    }

    /**
     * 결제 생성 성공 응답
     * Topic: order.payment.create.success
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_CREATE_SUCCESS)
    public void onPaymentCreateSuccess(ConsumerRecord<String, Object> record) {

        PaymentCreateEventRes event = objectMapper.convertValue(record.value(), PaymentCreateEventRes.class);

        log.info("[KAFKA][ORDER][PAYMENT_CREATE][CONSUMED] topic={} partition={} offset={} sagaId={} orderId={} paymentId={}",
                record.topic(), record.partition(), record.offset(),
                event.getSagaId(), event.getOrderId(), event.getPaymentId());

        orderService.registerPayment(event.getOrderId(), event.getPaymentId());
        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.PAYMENT_CREATED);
    }

    /**
     * 결제 생성 실패 응답
     * Topic: order.payment.create.failure
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_CREATE_FAILURE)
    public void onPaymentCreateFailure(ConsumerRecord<String, Object> record) {

        PaymentCreateEventRes event = objectMapper.convertValue(record.value(), PaymentCreateEventRes.class);

        log.info("[KAFKA][ORDER][PAYMENT_CREATE][CONSUMED] topic={} partition={} offset={} sagaId={} orderId={}",
                record.topic(), record.partition(), record.offset(),
                event.getSagaId(), event.getOrderId());

        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.PAYMENT_CREATED, "결제 생성 실패");

        log.warn("[SAGA][ORDER][PAYMENT_CREATE][FAILED] step={} sagaId={} orderId={} reason=BUSINESS_FAILURE",
                OrderSagaStep.PAYMENT_CREATED, event.getSagaId(), event.getOrderId());
    }

    // ====== payment event consumer  ======
    private static final String TYPE_PAYMENT_APPROVED = "PAYMENT_APPROVED";
    private static final String TYPE_PAYMENT_CANCELED = "PAYMENT_CANCELED";

    /**
     * 결제 승인 성공
     * topic: payment.order.approve.success
     */
    @KafkaListener(topics = KafkaTopics.PAYMENT_ORDER_APPROVE_SUCCESS)
    public void onPaymentApproveSuccess(ConsumerRecord<String, Object> record) {

        PaymentBaseEventRes event = toBaseEvent(record);

        log.info("[KAFKA][ORDER][PAYMENT_APPROVE][CONSUMED] topic={} partition={} offset={} type={} eventId={} orderId={} paymentId={}",
                record.topic(), record.partition(), record.offset(),
                event.getType(), event.getEventId(), event.getOrderId(), event.getPaymentId());

        if (!TYPE_PAYMENT_APPROVED.equals(event.getType())) {
            log.warn("[KAFKA][ORDER][PAYMENT_APPROVED][SKIP] unexpected type={} eventId={}",
                    event.getType(), event.getEventId());
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
    public void onPaymentCancelSuccess(ConsumerRecord<String, Object> record) {

        PaymentBaseEventRes event = toBaseEvent(record);

        log.info("[KAFKA][ORDER][PAYMENT_CANCELED][CONSUMED] topic={} partition={} offset={} type={} eventId={} orderId={} paymentId={}",
                record.topic(), record.partition(), record.offset(),
                event.getType(), event.getEventId(), event.getOrderId(), event.getPaymentId());

        if (!TYPE_PAYMENT_CANCELED.equals(event.getType())) {
            log.warn("[KAFKA][ORDER][PAYMENT_CANCELED][SKIP] unexpected type={} eventId={}",
                    event.getType(), event.getEventId());
            return;
        }

        PaymentCancelEventRes payload = readPayload(event, PaymentCancelEventRes.class);
        // 주문 취소 반영 (PAID -> CANCELED)
        orderService.cancelOrder(payload.toCommand());
    }

    // ===== payment 공통 파싱 유틸 =====
    private PaymentBaseEventRes toBaseEvent(ConsumerRecord<String, Object> record) {
        try {
            return objectMapper.convertValue(record.value(), PaymentBaseEventRes.class);
        } catch (IllegalArgumentException e) {
            log.error("[KAFKA][ORDER][PAYMENT][BASE_EVENT_CONVERT_FAILED] topic={} partition={} offset={} value={}",
                    record.topic(), record.partition(), record.offset(), record.value(), e);
            throw e;
        }
    }

    private <T> T readPayload(PaymentBaseEventRes event, Class<T> clazz) {
        try {
            return objectMapper.readValue(event.getPayloadJson(), clazz);
        } catch (JsonProcessingException e) {
            log.error("[KAFKA][ORDER][PAYMENT][PAYLOAD_DESERIALIZE_FAILED] type={} eventId={} payloadJson={}",
                    event.getType(), event.getEventId(), event.getPayloadJson(), e);
            throw new IllegalStateException(event.getType() + " payloadJson deserialize failed", e);
        }
    }
}