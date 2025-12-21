package com.palja.payment_service.application.event.dto;

import com.palja.payment_service.domain.event.vo.PaymentEventType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PaymentEventEnvelope(
        UUID eventId,
        PaymentEventType type,
        LocalDateTime occurredAt,
        UUID paymentId,
        UUID orderId,
        String payloadJson
) {
}
