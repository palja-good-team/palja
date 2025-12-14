package com.palja.order_service.application.command;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record CompleteOrderPaymentCommand(
        UUID orderId,
        UUID paymentId,
        BigDecimal paidAmount
) {
}