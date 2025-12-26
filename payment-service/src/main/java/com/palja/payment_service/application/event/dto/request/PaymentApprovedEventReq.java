package com.palja.payment_service.application.event.dto.request;

import com.palja.payment_service.application.event.dto.PaymentEvent;
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
) implements PaymentEvent {
}
