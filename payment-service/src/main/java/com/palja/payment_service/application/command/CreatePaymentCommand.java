package com.palja.payment_service.application.command;

import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.vo.PaymentMethod;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.vo.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;

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