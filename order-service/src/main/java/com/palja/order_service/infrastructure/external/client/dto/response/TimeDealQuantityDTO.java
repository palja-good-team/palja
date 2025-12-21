package com.palja.order_service.infrastructure.external.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDealQuantityDTO {
    private Long totalQuantity;
    private Long remainingQuantity;
}