package com.palja.payment_service.infrastructure.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TossPaymentConfirmReq {
    private String paymentKey;
    private String orderId;
    private BigDecimal amount;
}
