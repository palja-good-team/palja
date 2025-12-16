package com.palja.payment_service.infrastructure.external.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOrderPaymentDTO {
    private UUID paymentId;
    private BigDecimal paidAmount;
}
