package com.palja.payment_service.application.event.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentApprovedPayload(
        UUID paymentId,
        UUID orderId,
        Long userId,
        BigDecimal paidAmount,
        String paymentKey,
        String pgResponseCode,
        String pgResponseMessage,
        LocalDateTime approvedAt
) {
}
