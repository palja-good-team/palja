package com.palja.payment_service.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PGPaymentRes {

    private final String paymentKey;
    private final String pgResponseCode;
    private final String pgResponseMessage;
    private final boolean success;
    private final BigDecimal approvedAmount;
}
