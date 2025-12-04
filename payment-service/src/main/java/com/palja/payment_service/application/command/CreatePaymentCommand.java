package com.palja.payment_service.application.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;


@Builder
public record CreatePaymentCommand(
        UUID orderId,
        Long userId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String paymentKey
) {
}