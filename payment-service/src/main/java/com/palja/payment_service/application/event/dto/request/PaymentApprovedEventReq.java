package com.palja.payment_service.application.event.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentApprovedEventReq(
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
