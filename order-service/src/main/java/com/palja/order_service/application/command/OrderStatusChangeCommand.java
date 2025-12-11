package com.palja.order_service.application.command;

import lombok.Builder;

import java.util.UUID;

// 주문 상태 변경 Command
@Builder
public record OrderStatusChangeCommand(
        UUID orderId,
        String managerLoginId,
        String status,
        String reason
) {
}