package com.palja.product_service.application.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DecreaseStockTimeDealErrorEvent {

    private UUID productId;
    private String message;

    public static DecreaseStockTimeDealErrorEvent create(UUID productId, String message) {
        return new DecreaseStockTimeDealErrorEvent(productId, message);
    }
}
