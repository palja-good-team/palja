package com.palja.payment_service.infrastructure.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TossPaymentCancelReq {
    private String cancelReason;
    private BigDecimal cancelAmount;
}
