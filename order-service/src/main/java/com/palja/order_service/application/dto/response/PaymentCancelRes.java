package com.palja.order_service.application.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCancelRes {

    private UUID paymentId;
    private BigDecimal amount;
    private String status;

    public static PaymentCancelRes of(
            UUID paymentId,
            BigDecimal amount,
            String status
    ) {
        return PaymentCancelRes.builder()
                .paymentId(paymentId)
                .amount(amount)
                .status(status)
                .build();
    }
}