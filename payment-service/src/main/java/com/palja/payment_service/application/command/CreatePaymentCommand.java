package com.palja.payment_service.application.command;

import com.palja.payment_service.domain.vo.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;


@Builder
public record CreatePaymentCommand(
        UUID orderId,
        Long userId,
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        String paymentKey
) {
}