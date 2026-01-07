package com.palja.order_service.application.event.publisher;

import com.palja.order_service.application.event.dto.request.OrderCanceledEventReq;
import com.palja.order_service.application.event.dto.request.OrderCreatedEventReq;
import com.palja.order_service.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Order Internal Event Publisher
 *
 * - DB 트랜잭션 내부에서 Spring ApplicationEvent 발행
 * - 실제 Kafka 발행은 AFTER_COMMIT 리스너에서 수행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderInternalEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 주문 생성 이벤트 발행 (TX 내부)
     * - AFTER_COMMIT 리스너에서 Saga 시작 트리거
     */
    public void publishOrderCreated(UUID orderId, UUID sagaId) {
        log.info("주문 생성 내부 이벤트 발행 (order created event published): orderId={} sagaId={}",
                orderId, sagaId);

        eventPublisher.publishEvent(OrderCreatedEventReq.of(orderId, sagaId));
    }

    /**
     * 주문 취소 이벤트 발행 (TX 내부)
     * - AFTER_COMMIT 리스너에서 보상 Saga 트리거
     */
    public void publishOrderCanceled(Order order) {
        log.info("주문 취소 내부 이벤트 발행 (order canceled event published): orderId={}",
                order.getOrderId());

        eventPublisher.publishEvent(OrderCanceledEventReq.from(order));
    }
}