package com.palja.payment_service.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindPaymentListByConditionCommand(
        String status,
        Long userId,
        UUID orderId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
