package com.palja.order_service.application.dto;

import com.palja.order_service.domain.entity.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// 주문 생성 응답 DTO
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateOrderRes {
    private UUID orderId;
    private String status;
    private OrderItemRes orderItem;
    private DeliveryRes delivery;
    private PricingRes pricing;
    private CouponSnapshotRes coupon;
    private Boolean timeDealOrder;
    private Instant createdAt;

    // Order 엔티티 → 주문 생성 응답 DTO 변환
    public static CreateOrderRes from(Order order) {

        return CreateOrderRes.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .orderItem(OrderItemRes.from(order.getOrderItem()))
                .pricing(PricingRes.from(order))
                .coupon(CouponSnapshotRes.of(order.getCouponId(), order.getCouponName()))
                .delivery(DeliveryRes.from(order.getDelivery()))
                .timeDealOrder(order.isTimeDealOrder())
                .createdAt(order.getCreatedAt())
                .build();
    }
}