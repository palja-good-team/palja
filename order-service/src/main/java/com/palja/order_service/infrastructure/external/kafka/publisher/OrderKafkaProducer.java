package com.palja.order_service.infrastructure.external.kafka.publisher;

import com.palja.order_service.application.dto.event.OrderSagaEvent;
import com.palja.order_service.application.dto.event.request.*;
import com.palja.order_service.application.port.kafka.OrderEventPublisher;
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
 *
 * 역할:
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
    public void publishSagaStart(SagaStartEventReq event) {
        send(KafkaTopics.SAGA_START_REQUEST, event.getSagaId().toString(), event);
        log.info("[KAFKA][PUBLISH] topic={}, sagaId={}, orderId={}",
                KafkaTopics.SAGA_START_REQUEST, event.getSagaId(), event.getOrderId());
    }

    @Override
    public void publishStockDeduct(StockDeductEventReq event) {
        send(KafkaTopics.STOCK_DEDUCT_REQUEST, event.getSagaId().toString(), event);
        log.info("[KAFKA][PUBLISH] topic={}, sagaId={}", KafkaTopics.STOCK_DEDUCT_REQUEST, event.getSagaId());
    }

    @Override
    public void publishStockRestore(StockRestoreEventReq event) {
        send(KafkaTopics.STOCK_RESTORE_REQUEST, event.getSagaId().toString(), event);
        log.info("[KAFKA][PUBLISH] topic={}, sagaId={}", KafkaTopics.STOCK_RESTORE_REQUEST, event.getSagaId());
    }

    @Override
    public void publishCouponUse(CouponUseEventReq event) {
        send(KafkaTopics.COUPON_USE_REQUEST, event.getSagaId().toString(), event);
        log.info("[KAFKA][PUBLISH] topic={}, sagaId={}",
                KafkaTopics.COUPON_USE_REQUEST, event.getSagaId());
    }

    @Override
    public void publishCouponCancel(CouponCancelEventReq event) {
        send(KafkaTopics.COUPON_CANCEL_REQUEST, event.getSagaId().toString(), event);
        log.info("[KAFKA][PUBLISH] topic={}, sagaId={}", KafkaTopics.COUPON_CANCEL_REQUEST, event.getSagaId());
    }

    @Override
    public void publishPaymentCreate(PaymentCreateEventReq event) {
        send(KafkaTopics.PAYMENT_CREATE_REQUEST, event.getSagaId().toString(), event);
        log.info("[KAFKA][PUBLISH] topic={}, sagaId={}", KafkaTopics.PAYMENT_CREATE_REQUEST, event.getSagaId());
    }

//    @Override
//    public void publishOrderCancel(OrderCancelEventReq event) {
//        send(KafkaTopics.ORDER_CANCEL_REQUEST, event.getSagaId().toString(), event);
//        log.info("[KAFKA][PUBLISH] topic={}, sagaId={}", KafkaTopics.ORDER_CANCEL_REQUEST, event.getSagaId());
//    }

    /**
     * Kafka 전송 (공통 로직)
     */
    private void send(String topic, String key, Object event) {

        Message<Object> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key) // 병렬처리를 위해 -> 현재는 빼도됌
                .build();

        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[KAFKA][PUBLISH_FAILED] topic={}, key={}, error={}",
                                topic, key, ex.getMessage(), ex);
                    }
                });
    }
}