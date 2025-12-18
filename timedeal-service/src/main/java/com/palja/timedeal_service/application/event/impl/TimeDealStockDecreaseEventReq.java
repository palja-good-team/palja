package com.palja.timedeal_service.application.event.impl;

import com.palja.timedeal_service.application.event.TimeDealStockEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class TimeDealStockDecreaseEventReq implements TimeDealStockEvent {
    private final UUID timeDealId;
    private final UUID productId;
    private final long quantity;
}
