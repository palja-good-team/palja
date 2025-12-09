package com.palja.order_service.infrastructure.external.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelPaymentDTO {

    private BigDecimal cancelAmount;
    private String cancelReason;
}