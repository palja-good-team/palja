package com.palja.product_service.application.event.dto.request;

import com.palja.product_service.application.event.dto.ProductEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DecreaseStockTimeDealErrorEventReq implements ProductEvent {

    private UUID timeDealId;
    private UUID productId;
    private String message;

    public static DecreaseStockTimeDealErrorEventReq create(UUID timeDealId, UUID productId, String message) {
        return new DecreaseStockTimeDealErrorEventReq(timeDealId, productId, message);
    }
}
