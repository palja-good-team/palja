package com.palja.order_service.infrastructure.external.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealStockDecreaseDTO {
    private Long decreaseQuantity;
}
