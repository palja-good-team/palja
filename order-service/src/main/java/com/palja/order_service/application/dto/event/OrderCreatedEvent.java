package com.palja.order_service.application.dto.event;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 주문 생성 완료 이벤트 DTO
 * - 트랜잭션 커밋 후 외부 시스템 처리를 위한 이벤트
 * - Order 엔티티를 직접 참조하지 않음
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreatedEvent {

    // === 식별자 ===
    private UUID orderId;
    private Long userId;

    // === 주문 상품 ===
    private UUID productId;
    private int quantity;
    private UUID timeDealId;
    private boolean timeDealOrder;

    // === 쿠폰 ===
    private UUID couponUserId;
    private BigDecimal couponDiscountAmount;

    // === 금액 ===
    private BigDecimal finalAmount;

    // === 상태 ===
    private OrderStatus status;

    public static OrderCreatedEvent from(Order order) {
        return OrderCreatedEvent.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .productId(order.requireOrderItem().getProductId())
                .quantity(order.requireOrderItem().getQuantity())
                .timeDealId(order.requireOrderItem().getTimeDealId())
                .timeDealOrder(order.isTimeDealOrder())
                .couponUserId(order.getCouponUserId())
                .couponDiscountAmount(order.getOrderAmount().getCouponDiscountAmount())
                .finalAmount(order.getOrderAmount().getFinalAmount())
                .status(order.getStatus())
                .build();
    }
}