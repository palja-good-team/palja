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
public class SaleProductErrorEventReq implements ProductEvent {

    private UUID sagaId;
    private UUID orderId;

    public static SaleProductErrorEventReq create(UUID sagaId, UUID orderId) {
        return new SaleProductErrorEventReq(sagaId, orderId);
    }
}
