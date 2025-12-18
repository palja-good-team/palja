package com.palja.order_service.infrastructure.external.kafka.listener;

import com.palja.order_service.application.dto.event.request.SagaStartEventReq;
import com.palja.order_service.application.dto.event.response.*;
import com.palja.order_service.application.saga.OrderSagaOrchestrator;
import com.palja.order_service.application.saga.model.OrderSagaStep;
import com.palja.order_service.application.service.OrderService;
import com.palja.order_service.domain.repository.OrderRepository;
import com.palja.order_service.infrastructure.external.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
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
public class SagaKafkaListeners {

    private final OrderSagaOrchestrator orchestrator;
    private final OrderService orderService;

    /**
     * Saga 시작 이벤트 수신
     */
    @KafkaListener(
            topics = KafkaTopics.ORDER_SAGA_START,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onSagaStart(SagaStartEventReq event) {
        log.info("[KAFKA][CONSUME] topic={}, sagaId={}, orderId={}",
                KafkaTopics.ORDER_SAGA_START, event.getSagaId(), event.getOrderId());

        orchestrator.startSaga(event.getSagaId());
        // retry 로직 추가
    }

    /**
     * 재고 차감 성공 응답
     * Topic: order.stock.deduct.success
     */
    @KafkaListener(
            topics = KafkaTopics.STOCK_DEDUCT_SUCCESS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onStockDeductSuccess(StockDeductEventRes event) {
        log.info("[KAFKA][CONSUME][SUCCESS] topic={}, sagaId={}",
                KafkaTopics.STOCK_DEDUCT_SUCCESS, event.getSagaId());
        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.STOCK_RESERVED);
    }

    /**
     * 재고 차감 실패 응답
     * Topic: order.stock.deduct.failure
     */
    @KafkaListener(
            topics = KafkaTopics.STOCK_DEDUCT_FAILURE,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onStockDeductFailure(StockDeductEventRes event) {
        log.error("[KAFKA][CONSUME][FAILURE] topic={}, sagaId={}",
                KafkaTopics.STOCK_DEDUCT_FAILURE, event.getSagaId());
            orchestrator.failSaga(event.getSagaId(), OrderSagaStep.STOCK_RESERVED, "재고 차감 실패");
    }

    /**
     * 쿠폰 사용 성공 응답
     * Topic: coupon.order.use.success
     */
    @KafkaListener(
            topics = KafkaTopics.COUPON_USE_SUCCESS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onCouponUseSuccess(CouponUseEventRes event) {
        log.info("[KAFKA][CONSUME][SUCCESS] topic={}, sagaId={}",
                KafkaTopics.COUPON_USE_SUCCESS, event.getSagaId());
        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.COUPON_APPLIED);
    }

    /**
     * 쿠폰 사용 실패 응답
     * Topic: coupon.order.use.failure
     */
    @KafkaListener(
            topics = KafkaTopics.COUPON_USE_FAILURE,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onCouponUseFailure(CouponUseEventRes event) {
        log.error("[KAFKA][CONSUME][FAILURE] topic={}, sagaId={}",
                KafkaTopics.COUPON_USE_FAILURE, event.getSagaId());
        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.COUPON_APPLIED, "쿠폰 사용 실패");
    }

    /**
     * 결제 생성 성공 응답
     * Topic: payment.order.create.success
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_CREATE_SUCCESS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onPaymentCreateSuccess(PaymentCreateEventRes event) {
        log.info("[KAFKA][CONSUME][SUCCESS] topic={}, sagaId={}, paymentId={}",
                KafkaTopics.PAYMENT_CREATE_SUCCESS, event.getSagaId(), event.getPaymentId());
        orderService.registerPayment(event.getOrderId(), event.getPaymentId());
        orchestrator.continueAfterStep(event.getSagaId(), OrderSagaStep.PAYMENT_CREATED);
    }

    /**
     * 결제 생성 실패 응답
     * Topic: payment.order.create.failure
     */
    @KafkaListener(
            topics = KafkaTopics.PAYMENT_CREATE_FAILURE,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onPaymentCreateFailure(PaymentCreateEventRes event) {
        log.error("[KAFKA][CONSUME][FAILURE] topic={}, sagaId={}",
                KafkaTopics.PAYMENT_CREATE_FAILURE, event.getSagaId());
        orchestrator.failSaga(event.getSagaId(), OrderSagaStep.PAYMENT_CREATED, "결제 생성 실패");
    }
}