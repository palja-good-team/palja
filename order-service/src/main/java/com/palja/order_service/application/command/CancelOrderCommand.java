package com.palja.order_service.application.command;

import com.palja.common.vo.UserRole;
import lombok.Builder;

import java.util.UUID;

// 주문 취소 Command
@Builder
public record CancelOrderCommand(
        UUID orderId,
        String CurrentUserLoginId,
        UserRole CurrentUserRole,
        String cancelReason
) {
}