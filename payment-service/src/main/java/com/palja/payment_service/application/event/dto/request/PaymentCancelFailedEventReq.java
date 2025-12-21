package com.palja.payment_service.application.event.dto.request;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentCancelFailedEventReq(
        UUID paymentId,
        UUID orderId,
        Long userId,
        BigDecimal amount,
        String cancelReason,
        String paymentKey,
        String pgResponseCode,
        String pgResponseMessage,
        LocalDateTime failedAt
) {
}
