package com.palja.order_service.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRes {

    private final UUID paymentId;
    private final BigDecimal amount;

    public static PaymentRes of(UUID paymentId, BigDecimal amount) {
        return PaymentRes.builder()
                .paymentId(paymentId)
                .amount(amount)
                .build();
    }
}