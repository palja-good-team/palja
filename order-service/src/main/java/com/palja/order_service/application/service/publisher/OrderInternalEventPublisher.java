package com.palja.order_service.application.service.publisher;

import com.palja.order_service.application.dto.event.request.OrderCanceledEventReq;
import com.palja.order_service.application.dto.event.request.OrderCreatedEventReq;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderInternalEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    // Spring ApplicationEvent 발행
    // 실제 Kafka 발행은 리스너가 트랜잭션 커밋 후 처리

    // 주문 생성 이벤트 발행
    public void publishOrderCreated(UUID orderId, UUID sagaId) {
        eventPublisher.publishEvent(OrderCreatedEventReq.of(orderId, sagaId));
        log.info("[INTERNAL_EVENT][PUBLISHED] OrderCreated: orderId={}", orderId);

    }

    // 주문 취소 이벤트 발행
    public void publishOrderCanceled(Order order) {
        eventPublisher.publishEvent(OrderCanceledEventReq.from(order));
        log.info("[INTERNAL_EVENT][PUBLISHED] OrderCanceled: orderId={}", order.getOrderId());

    }
}