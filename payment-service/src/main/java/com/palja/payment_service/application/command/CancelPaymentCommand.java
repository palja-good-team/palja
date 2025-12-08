package com.palja.payment_service.application.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CancelPaymentCommand(
        UUID paymentId,
        String loginId,
        BigDecimal cancelAmount,
        String cancelReason
) {
}
