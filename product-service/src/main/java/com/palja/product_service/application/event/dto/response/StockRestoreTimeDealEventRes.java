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
public class StockRestoreTimeDealEventRes implements TimeDealEvent {

    private UUID sagaId;
    private UUID orderId;
    private UUID productId;
    private UUID timeDealId;
    private Long quantity;
    private Boolean isTimeDeal;
}
