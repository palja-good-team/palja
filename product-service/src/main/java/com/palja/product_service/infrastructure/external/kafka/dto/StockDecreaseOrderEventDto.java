package com.palja.product_service.infrastructure.external.kafka.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockDecreaseOrderEventDto {

    private UUID sagaId;
    private UUID orderId;
    private UUID productId;
    private UUID timeDealId;
    private Long quantity;
    private Boolean isTimeDeal;
}
