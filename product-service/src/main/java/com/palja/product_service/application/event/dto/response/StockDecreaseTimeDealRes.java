package com.palja.product_service.application.event.dto.response;

import com.palja.product_service.application.event.dto.TimeDealEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StockDecreaseTimeDealRes implements TimeDealEvent {

    private UUID timeDealId;
    private UUID productId;
    private Long quantity;
}
