package com.palja.order_service.infrastructure.external.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UseCouponDTO {
    private UUID orderId;
    private BigDecimal discountAmount;
}