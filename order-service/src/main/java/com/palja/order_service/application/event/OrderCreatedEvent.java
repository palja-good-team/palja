package com.palja.order_service.application.event;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 주문 생성 완료 이벤트
 * - 트랜잭션 커밋 후 외부 시스템 처리를 위한 이벤트
 * - Order 엔티티에서 필요한 모든 정보를 조회 가능
 */
public record OrderCreatedEvent(
        Order order
) {
    public static OrderCreatedEvent from(Order order) {
        return new OrderCreatedEvent(order);
    }

    // 편의 메서드
    public UUID getOrderId() {
        return order.getOrderId();
    }

    public UUID getProductId() {
        return order.requireOrderItem().getProductId();
    }

    public int getQuantity() {
        return order.requireOrderItem().getQuantity();
    }

    public UUID getTimeDealId() {
        return order.requireOrderItem().getTimeDealId();
    }

    public boolean isTimeDealOrder() {
        return order.isTimeDealOrder();
    }

    public UUID getCouponUserId() {
        return order.getCouponUserId();
    }

    public Long getUserId() {
        return order.getUserId();
    }

    public BigDecimal getFinalAmount() {
        return order.getOrderAmount().getFinalAmount();
    }

    public OrderStatus getStatus() {
        return order.getStatus();
    }

    public BigDecimal getCouponDiscountAmount() {
        return order.getOrderAmount().getCouponDiscountAmount();
    }
}