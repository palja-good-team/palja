package com.palja.payment_service.application.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindPaymentLogListByConditionCommand(
        UUID paymentId,
        UUID orderId,
        String status,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
