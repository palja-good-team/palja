package com.palja.timedeal_service.application.event.dto.request.in;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeDealStockDecreaseEventReq {

    private UUID sagaId;
    private UUID orderId;

    private UUID productId;
    private UUID timeDealId;
    private Long quantity;

    private boolean isTimeDeal;
}