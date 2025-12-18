package com.palja.order_service.infrastructure.external.kafka.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.order_service.application.dto.event.request.SagaStartEventReq;
import com.palja.order_service.application.dto.event.response.CouponUseEventRes;
import com.palja.order_service.application.dto.event.response.PaymentCreateEventRes;
import com.palja.order_service.application.dto.event.response.StockDeductEventRes;
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

        Object value = record.value();
        SagaStartEventReq event = objectMapper.convertValue(value, SagaStartEventReq.class);

        log.info("[KAFKA][SAGA][START] topic={}, sagaId={}", KafkaTopics.SAGA_START_REQUEST, event.getSagaId());

        orchestrator.startSaga(event.getSagaId());
    }

    /**
     * 재고 차감 성공 응답
     * Topic: order.stock.deduct.success
     */
    @KafkaListener(
            topics = KafkaTopics.STOCK_DEDUCT_SUCCESS
    )
    public void onStockDeductSuccess(ConsumerRecord<String, Object> record) {

        Object value = record.value();
        StockDeductEventRes event = objectMapper.convertValue(value, StockDeductEventRes.class);

        log.info("[KAFKA][SAGA][STEP][SUCCESS] topic={}, sagaId={}",
                KafkaTopics.STOCK_DEDUCT_SUCCESS, event.getSagaId());

        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.STOCK_RESERVED);
    }

    /**
     * 재고 차감 실패 응답
     * Topic: order.stock.deduct.failure
     */
    @KafkaListener(
            topics = KafkaTopics.STOCK_DEDUCT_FAILURE
    )
    public void onStockDeductFailure(ConsumerRecord<String, Object> record) {

        Object value = record.value();
        StockDeductEventRes event = objectMapper.convertValue(value, StockDeductEventRes.class);

        log.error("[KAFKA][SAGA][STEP][FAILURE] topic={}, sagaId={}",
                KafkaTopics.STOCK_DEDUCT_FAILURE, event.getSagaId());

            orchestrator.failSaga(event.getSagaId(), OrderSagaStep.STOCK_RESERVED, "재고 차감 실패");
    }

    /**
     * 쿠폰 사용 성공 응답
     * Topic: order.coupon.use.success
     */
    @KafkaListener(
            topics = KafkaTopics.COUPON_USE_SUCCESS
    )
    public void onCouponUseSuccess(ConsumerRecord<String, Object> record) {

        Object value = record.value();
        CouponUseEventRes event = objectMapper.convertValue(value, CouponUseEventRes.class);

        log.info("[KAFKA][SAGA][STEP][SUCCESS] topic={}, sagaId={}",
                KafkaTopics.COUPON_USE_SUCCESS, event.getSagaId());

        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.COUPON_APPLIED);
    }

    /**
     * 쿠폰 사용 실패 응답
     * Topic: order.coupon.use.failure
     */
    @KafkaListener(
            topics = KafkaTopics.COUPON_USE_FAILURE
    )
    public void onCouponUseFailure(ConsumerRecord<String, Object> record) {

        Object value = record.value();
        CouponUseEventRes event = objectMapper.convertValue(value, CouponUseEventRes.class);

        log.error("[KAFKA][SAGA][STEP][FAILURE] topic={}, sagaId={}",
                KafkaTopics.COUPON_USE_FAILURE, event.getSagaId());

        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.COUPON_APPLIED, "쿠폰 사용 실패");
    }

    /**
     * 결제 생성 성공 응답
     * Topic: order.payment.create.success
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_CREATE_SUCCESS
    )
    public void onPaymentCreateSuccess(ConsumerRecord<String, Object> record) {

        Object value = record.value();
        PaymentCreateEventRes event = objectMapper.convertValue(value, PaymentCreateEventRes.class);

        log.info("[KAFKA][SAGA][STEP][SUCCESS] topic={}, sagaId={}, paymentId={}",
                KafkaTopics.PAYMENT_CREATE_SUCCESS, event.getSagaId(), event.getPaymentId());

        orderService.registerPayment(event.getOrderId(), event.getPaymentId());
        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.PAYMENT_CREATED);
    }

    /**
     * 결제 생성 실패 응답
     * Topic: order.payment.create.failure
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_CREATE_FAILURE
    )
    public void onPaymentCreateFailure(ConsumerRecord<String, Object> record) {

        Object value = record.value();
        PaymentCreateEventRes event = objectMapper.convertValue(value, PaymentCreateEventRes.class);

        log.error("[KAFKA][SAGA][STEP][FAILURE] topic={}, sagaId={}",
                KafkaTopics.PAYMENT_CREATE_FAILURE, event.getSagaId());

        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.PAYMENT_CREATED, "결제 생성 실패");
    }
}