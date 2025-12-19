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

    /**
     * TX 내부에서 주문 생성 이벤트 발행
     * - 실제 Kafka 발행은 AFTER_COMMIT 리스너에서 수행
     */
    public void publishOrderCreated(UUID orderId, UUID sagaId) {
        // TX : DB 트랜잭션 내부
        log.info("[TX][ORDER][SAGA_START][READY] orderId={} sagaId={}", orderId, sagaId);

        eventPublisher.publishEvent(OrderCreatedEventReq.of(orderId, sagaId));
    }

    /**
     * TX 내부에서 주문 취소 이벤트 발행
     * - 커밋 후 보상 Saga 트리거
     */
    public void publishOrderCanceled(Order order) {
        log.info("[TX][ORDER][ORDER_CANCEL][READY] orderId={}", order.getOrderId());

        eventPublisher.publishEvent(OrderCanceledEventReq.from(order));
    }
}