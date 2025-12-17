package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// 고객용 주문 목록 조회 응답
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomerOrderSummaryRes {

    private UUID orderId;
    private String status;
    private String productName;
    private Long quantity;
    private BigDecimal finalAmount;
    private boolean timeDealOrder;
    private LocalDateTime createdAt;

    public static CustomerOrderSummaryRes from(Order order) {
        return CustomerOrderSummaryRes.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .productName(order.getOrderItem().getProductName())
                .quantity(order.getOrderItem().getQuantity())
                .finalAmount(order.getOrderAmount().getFinalAmount())
                .timeDealOrder(order.isTimeDealOrder())
                .createdAt(order.getCreatedAt())
                .build();
    }
}