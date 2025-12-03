package com.palja.payment_service.application.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CancelPaymentCommand(
        UUID paymentId,
        Long userId,
        BigDecimal cancelAmount,
        String cancelReason
) {
}
