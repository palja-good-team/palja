package com.palja.order_service.infrastructure.external.kafka.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.order_service.application.dto.event.request.SagaStartEventReq;
import com.palja.order_service.application.dto.event.response.CouponUseEventRes;
import com.palja.order_service.application.dto.event.response.PaymentCreateEventRes;
import com.palja.order_service.application.dto.event.response.StockDecreaseEventRes;
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

        StockDecreaseEventRes event = objectMapper.convertValue(record.value(), StockDecreaseEventRes.class);

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

        StockDecreaseEventRes event = objectMapper.convertValue(record.value(), StockDecreaseEventRes.class);

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

        CouponUseEventRes event = objectMapper.convertValue(record.value(), CouponUseEventRes.class);

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

        CouponUseEventRes event = objectMapper.convertValue(record.value(), CouponUseEventRes.class);

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
}