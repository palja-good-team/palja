package com.palja.order_service.application.event.dto.request;

import com.palja.order_service.application.event.dto.OrderEvent;
import com.palja.order_service.domain.entity.Order;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCanceledEventReq implements OrderEvent {
    private UUID orderId;
    private Long userId;

    private String cancelReason;
    private String canceledBy;

    // 보상 트랜잭션에 필요한 모든 데이터
    private UUID productId;
    private UUID timeDealId;
    private Long quantity;
    private UUID couponUserId;
    private UUID paymentId;
    private BigDecimal amount;

    public static OrderCanceledEventReq from(Order order) {
        return OrderCanceledEventReq.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .cancelReason(order.getCancellation().getCancelReason())
                .canceledBy(order.getCancellation().getCanceledBy())
                .productId(order.getOrderItem().getProductId())
                .timeDealId(order.getOrderItem().getTimeDealId())
                .quantity(order.getOrderItem().getQuantity())
                .couponUserId(order.getCouponUserId())
                .paymentId(order.getPaymentId())
                .amount(order.getOrderAmount().getFinalAmount())
                .build();
    }
}