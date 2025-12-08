package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderStatusChangeRes {

    private UUID orderId;
    private String previousStatus;
    private String currentStatus;
    private String reason;
    private String changedBy;
    private LocalDateTime changedAt;

    public static OrderStatusChangeRes from(Order order, String previousStatus, String reason, String changedBy) {
        return OrderStatusChangeRes.builder()
                .orderId(order.getOrderId())
                .previousStatus(previousStatus)
                .currentStatus(order.getStatus().name())
                .reason(reason)
                .changedBy(changedBy)
                .changedAt(order.getUpdatedAt())
                .build();
    }
}