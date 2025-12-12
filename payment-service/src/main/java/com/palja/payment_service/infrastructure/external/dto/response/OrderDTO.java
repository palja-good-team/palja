package com.palja.payment_service.infrastructure.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private UUID orderId;
    private Long userId;
    private String status;
    private Pricing pricing;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal finalAmount;
    }

    public BigDecimal getFinalAmount() {
        return pricing != null ? pricing.finalAmount : null;
    }
}
