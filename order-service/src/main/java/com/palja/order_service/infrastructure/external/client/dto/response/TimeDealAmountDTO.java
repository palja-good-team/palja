package com.palja.order_service.infrastructure.external.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealAmountDTO {
    private BigDecimal originalPrice;
    private BigDecimal timeDealPrice;
    private int discountRate;
}