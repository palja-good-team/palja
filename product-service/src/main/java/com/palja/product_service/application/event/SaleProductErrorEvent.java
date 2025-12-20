package com.palja.product_service.application.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SaleProductErrorEvent {

    private UUID sagaId;
    private UUID orderId;

    public static SaleProductErrorEvent create(UUID sagaId, UUID orderId) {
        return new SaleProductErrorEvent(sagaId, orderId);
    }
}
