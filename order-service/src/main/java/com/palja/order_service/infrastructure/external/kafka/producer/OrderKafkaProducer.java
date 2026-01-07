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
 * - OrderEventPublisher 구현
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
        send(KafkaTopics.STOCK_DECREASE_REQUEST, event.getSagaId().toString(), event, event.getOrderId().toString());
    }

    @Override
    public void publishStockRestore(StockRestoreEventReq event) {
        send(KafkaTopics.STOCK_RESTORE_REQUEST, event.getSagaId().toString(), event, event.getOrderId().toString());
    }

    @Override
    public void publishCouponUse(CouponUseEventReq event) {
        send(KafkaTopics.COUPON_USE_REQUEST, event.getSagaId().toString(), event, event.getOrderId().toString());
    }

    @Override
    public void publishCouponCancel(CouponCancelEventReq event) {
        send(KafkaTopics.COUPON_CANCEL_REQUEST, event.getSagaId().toString(), event, event.getOrderId().toString());
    }

    @Override
    public void publishPaymentCreate(PaymentCreateEventReq event) {
        send(KafkaTopics.PAYMENT_CREATE_REQUEST, event.getSagaId().toString(), event, event.getOrderId().toString());
    }

    @Override
    public void publishPaymentCancel(PaymentCancelEventReq event) {
        send(KafkaTopics.PAYMENT_CANCEL_REQUEST, event.getSagaId().toString(), event, event.getOrderId().toString());
    }

    @Override
    public void publishOrderCanceled(OrderCanceledEventReq event) {
        // 주문 취소는 sagaId가 없을 수 있으니 orderId 기반 key
        send(KafkaTopics.ORDER_CANCEL_REQUEST, event.getOrderId().toString(), event, event.getOrderId().toString());
    }

    /**
     * Kafka 전송 (공통 로직)
     */
    private void send(String topic, String key, Object payload, String orderId) {

        String safeOrderId = (orderId == null ? "N/A" : orderId);

        Message<Object> message = MessageBuilder
                .withPayload(payload)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(KafkaHeaders.KEY, key) // 키 기반 파티셔닝/순서 보장
                .build();

        log.info("Kafka 전송 요청 수락 (kafka publish accepted): topic={} key={} orderId={}", topic, key, safeOrderId);

        kafkaTemplate.send(message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka 전송 실패 (kafka publish failed): topic={} key={} orderId={} errorType={}",
                                topic, key, safeOrderId, ex.getClass().getSimpleName(), ex);
                    }
                });
    }
}