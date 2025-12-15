package com.palja.order_service.application.service.publisher;

import com.palja.order_service.application.dto.event.OrderCreatedSagaEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderSagaEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 주문 생성 이벤트 발행
     *
     * [이벤트 처리 흐름]
     * 1. @TransactionalEventListener(AFTER_COMMIT) 으로 트랜잭션 커밋 후 발행
     * 2. @Async로 비동기 처리
     * 3. OrderCreatedEventListener가 Saga Orchestrator 실행
     *
     * TODO: Kafka Producer로 전환 예정
     * - Topic: order-created-events
     */
    public void publishOrderCreated(UUID orderId) {
        eventPublisher.publishEvent(OrderCreatedSagaEvent.of(orderId));
    }
}