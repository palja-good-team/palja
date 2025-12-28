package com.palja.timedeal_service.application.event.dto.response;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeDealStockDecreaseEventRes {

    private UUID sagaId;
    private UUID orderId;

    public static TimeDealStockDecreaseEventRes success(UUID sagaId, UUID orderId) {
        return new TimeDealStockDecreaseEventRes(sagaId, orderId);
    }

    public static TimeDealStockDecreaseEventRes failure(UUID sagaId, UUID orderId) {
        return new TimeDealStockDecreaseEventRes(sagaId, orderId);
    }
}