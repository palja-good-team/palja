package com.palja.order_service.application.service.publisher;

import com.palja.order_service.application.dto.event.request.OrderCreatedEventReq;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderInternalEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    // Spring ApplicationEvent 발행
    // 실제 Kafka 발행은 리스너가 트랜잭션 커밋 후 처리
    public void publishOrderCreated(UUID orderId, UUID sagaId) {
        eventPublisher.publishEvent(new OrderCreatedEventReq(orderId, sagaId));
    }
}