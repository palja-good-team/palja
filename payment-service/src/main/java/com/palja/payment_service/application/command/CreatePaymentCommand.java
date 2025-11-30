package com.palja.payment_service.application.command;

import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.vo.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentCommand(
        UUID orderId,
        Long userId,
        BigDecimal amount,
        String currency,
        PaymentMethod paymentMethod,
        String paymentKey
) {
    public Payment toEntity(){
        return Payment.builder()
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .currency(currency)
                .paymentMethod(paymentMethod)
                .paymentKey(paymentKey)
                .build();
    }
}
