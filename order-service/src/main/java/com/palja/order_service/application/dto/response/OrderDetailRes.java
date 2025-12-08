package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDetailRes {

    private final UUID orderId;
    private final Long userId;
    private final OrderStatus status;

    private final OrderItemRes orderItem;
    private final DeliveryRes delivery;
    private final PricingRes pricing;
    private final CouponSnapshotRes coupon;

    private final UUID paymentId;
    private final boolean timeDealOrder;

    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static OrderDetailRes from(Order order) {
        return OrderDetailRes.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .status(order.getStatus())
                .orderItem(OrderItemRes.from(order.requireOrderItem()))
                .delivery(DeliveryRes.from(order.requireDelivery()))
                .pricing(PricingRes.from(order))
                .coupon(order.getCouponId() != null ? CouponSnapshotRes.of(order.getCouponId(), order.getCouponName()) : null)
                .paymentId(order.getPaymentId())
                .timeDealOrder(order.isTimeDealOrder())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}