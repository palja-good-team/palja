package com.palja.product_service.application.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChangePriceEvent implements ProductEvent{

    private UUID productId;
    private Long price;

    public static ChangePriceEvent create(UUID productId, Long price) {
        return new ChangePriceEvent(productId, price);
    }
}
