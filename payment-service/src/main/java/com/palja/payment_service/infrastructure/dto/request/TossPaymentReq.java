package com.palja.payment_service.infrastructure.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TossPaymentReq {
    private String orderId;
    private BigDecimal amount;
    private String currency;
    private String method;
}
