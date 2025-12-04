package com.palja.payment_service.application.command;

import com.palja.payment_service.domain.vo.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record FindPaymentListByConditionCommand(
        PaymentStatus status,
        Long userId,
        UUID orderId,
        LocalDateTime startDate,
        LocalDateTime endDate
) {
}
