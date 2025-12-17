package com.palja.payment_service.application.event;

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
