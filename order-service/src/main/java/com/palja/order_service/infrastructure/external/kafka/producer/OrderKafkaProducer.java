package com.palja.order_service.infrastructure.external.kafka.producer;

import com.palja.order_service.application.event.dto.request.*;
import com.palja.order_service.application.port.OrderEventPublisher;
import com.palja.order_service.infrastructure.external.kafka.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * Kafka Saga Event Publisher (Adapter)
 * - SagaEventPublisher 인터페이스 구현
 * - Kafka로 이벤트 발행
 * - sagaId를 Key로 사용 (파티셔닝)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaProducer implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publishStockDecrease(StockDecreaseEventReq event) {
        log.info("[KAFKA][ORDER][STOCK_DECREASE][PUBLISHED] topic={} orderId={} sagaId={}",
                KafkaTopics.STOCK_DECREASE_REQUEST, event.getOrderId(), event.getSagaId());

        send(KafkaTopics.STOCK_DECREASE_REQUEST, event.getSagaId().toString(), event);
    }

    @Override
    public void publishStockRestore(StockRestoreEventReq event) {
        log.info("[KAFKA][ORDER][STOCK_RESTORE][PUBLISHED] topic={} orderId={} sagaId={}",
                KafkaTopics.STOCK_RESTORE_REQUEST, event.getOrderId(), event.getSagaId());

        send(KafkaTopics.STOCK_RESTORE_REQUEST, event.getSagaId().toString(), event);
    }

    @Override
    public void publishCouponUse(CouponUseEventReq event) {
        log.info("[KAFKA][ORDER][COUPON_USE][PUBLISHED] topic={} orderId={} sagaId={}",
                KafkaTopics.COUPON_USE_REQUEST, event.getOrderId(), event.getSagaId());

        send(KafkaTopics.COUPON_USE_REQUEST, event.getSagaId().toString(), event);
    }

    @Override
    public void publishCouponCancel(CouponCancelEventReq event) {
        log.info("[KAFKA][ORDER][COUPON_CANCEL][PUBLISHED] topic={} orderId={} sagaId={}",
                KafkaTopics.COUPON_CANCEL_REQUEST, event.getOrderId(), event.getSagaId());

        send(KafkaTopics.COUPON_CANCEL_REQUEST, event.getSagaId().toString(), event);
    }

    @Override
    public void publishPaymentCreate(PaymentCreateEventReq event) {
        log.info("[KAFKA][ORDER][PAYMENT_CREATE][PUBLISHED] topic={} orderId={} sagaId={}",
                KafkaTopics.PAYMENT_CREATE_REQUEST, event.getOrderId(), event.getSagaId());

        send(KafkaTopics.PAYMENT_CREATE_REQUEST, event.getSagaId().toString(), event);
    }

    @Override
    public void publishPaymentCancel(PaymentCancelEventReq event) {
        log.info("[KAFKA][ORDER][PAYMENT_CANCEL][PUBLISHED] topic={} orderId={} sagaId={}",
                KafkaTopics.PAYMENT_CANCEL_REQUEST, event.getOrderId(), event.getSagaId());

        send(KafkaTopics.PAYMENT_CANCEL_REQUEST, event.getSagaId().toString(), event);
    }

    @Override
    public void publishOrderCanceled(OrderCanceledEventReq event) {
        log.info("[KAFKA][ORDER][ORDER_CANCEL][PUBLISHED] topic={} orderId={}",
                KafkaTopics.ORDER_CANCEL_REQUEST, event.getOrderId());

        send(KafkaTopics.ORDER_CANCEL_REQUEST, event.getOrderId().toString(), event);
    }

    /**
     * Kafka 전송 (공통 로직)
     * - 실패 로그만 기록
     */
    private void send(String topic, String key, Object payload) {

        Message<Object> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key) // 키 기반 파티셔닝/순서 보장 용도
                .build();

        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        // 실패만 기록
                        log.error("[KAFKA][ORDER][PUBLISH][FAILED] topic={} key={} reason={}",
                                topic, key, ex.getClass().getSimpleName(), ex);
                    }
                });
    }
}