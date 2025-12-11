package com.palja.payment_service.application.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record CompletePaymentCommand(
        UUID paymentId,
        String paymentKey,
        String loginId
) {
}
