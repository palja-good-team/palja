package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCancelRes {

    private final UUID orderId;
    private final String status;              // "CANCELED"
    private final String cancelReason;
    private final LocalDateTime canceledAt;
    private final String canceledBy;
    private final BigDecimal refundAmount;

    public static OrderCancelRes from(Order order) {
        return OrderCancelRes.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .cancelReason(order.getCancellation().getCancelReason())
                .canceledAt(order.getCancellation().getCanceledAt())
                .canceledBy(order.getCancellation().getCanceledBy())
                .refundAmount(order.getOrderAmount().getFinalAmount())
                .build();
    }
}