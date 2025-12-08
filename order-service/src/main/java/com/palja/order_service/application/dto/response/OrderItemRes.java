package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.OrderItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

// 주문 상품 정보
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderItemRes {
    private UUID orderItemId;
    private UUID productId;
    private String productName;
    private BigDecimal unitPrice;
    private int quantity;
    private BigDecimal lineTotalAmount;
    private UUID timeDealId;
    private BigDecimal timeDealPrice;
    private BigDecimal timeDealDiscountAmount;

    public static OrderItemRes from(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        return OrderItemRes.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProductId())
                .productName(orderItem.getProductName())
                .unitPrice(orderItem.getUnitPrice())
                .quantity(orderItem.getQuantity())
                .lineTotalAmount(orderItem.getLineTotalAmount())
                .timeDealId(orderItem.getTimeDealId())
                .timeDealPrice(orderItem.getTimeDealPrice())
                .timeDealDiscountAmount(orderItem.getTimeDealDiscountAmount())
                .build();
    }
}