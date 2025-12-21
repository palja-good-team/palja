package com.palja.timedeal_service.application.event.order.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class StockDeductEventRes {

    private UUID sagaId;
    private UUID orderId;

    public static StockDeductEventRes success(UUID sagaId, UUID orderId) {
        return new StockDeductEventRes(sagaId, orderId);
    }

    public static StockDeductEventRes failure(UUID sagaId, UUID orderId) {
        return new StockDeductEventRes(sagaId, orderId);
    }
}