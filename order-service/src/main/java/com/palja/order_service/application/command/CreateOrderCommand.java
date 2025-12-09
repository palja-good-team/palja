package com.palja.order_service.application.command;

import lombok.Builder;

import java.util.UUID;

// 주문 생성 Command
// Presentation -> Application 전달
@Builder
public record CreateOrderCommand(
        String loginId,
        UUID productId,
        int quantity,
        UUID timeDealId,
        UUID couponId,
        String paymentKey,
        String paymentMethod,
        DeliveryCommand delivery
) {
}