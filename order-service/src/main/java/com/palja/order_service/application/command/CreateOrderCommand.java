package com.palja.order_service.application.command;

import com.palja.common.vo.UserRole;
import lombok.Builder;

import java.util.UUID;

// 주문 생성 Command
// Presentation -> Application 전달
@Builder
public record CreateOrderCommand(
        String loginId,
        UserRole userRole,
        UUID productId,
        int quantity,
        UUID timeDealId,
        UUID couponUserId,
        DeliveryCommand delivery
) {
}