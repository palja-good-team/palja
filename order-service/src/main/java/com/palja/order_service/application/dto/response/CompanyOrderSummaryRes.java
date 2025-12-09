package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// 판매자용 주문 목록 조회 응답
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CompanyOrderSummaryRes {

    private final UUID orderId;
    private final OrderStatus status;

    private final String productName;
    private final Integer quantity;
    private final BigDecimal productAmount;

    private final String recipientName;
    private final String recipientAddress;

    private final Boolean timeDealOrder;
    private final LocalDateTime createdAt;

    public static CompanyOrderSummaryRes from(Order order) {
        return CompanyOrderSummaryRes.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus())
                .productName(order.getOrderItem().getProductName())
                .quantity(order.getOrderItem().getQuantity())
                .productAmount(order.getOrderItem().getLineTotalAmount())
                .recipientName(order.getDelivery().getRecipient().getName())
                .recipientAddress(order.getDelivery().getRecipient().getAddress())
                .timeDealOrder(order.isTimeDealOrder())
                .createdAt(order.getCreatedAt())
                .build();
    }
}