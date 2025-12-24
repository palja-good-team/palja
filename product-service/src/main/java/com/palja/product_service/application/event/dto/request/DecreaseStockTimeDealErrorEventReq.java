package com.palja.product_service.application.event.dto.request;

import com.palja.product_service.application.event.dto.TimeDealEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DecreaseStockTimeDealErrorEventReq implements TimeDealEvent {

    private UUID productId;
    private String message;

    public static DecreaseStockTimeDealErrorEventReq create(UUID productId, String message) {
        return new DecreaseStockTimeDealErrorEventReq(productId, message);
    }
}
