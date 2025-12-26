package com.palja.product_service.infrastructure.external.kafka.dto;

import com.palja.product_service.application.event.dto.OrderEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockDecreaseOrderDto implements OrderEvent {

    private UUID sagaId;
    private UUID orderId;
    private UUID productId;
    private UUID timeDealId;
    private Long quantity;
    private Boolean isTimeDeal;
}
