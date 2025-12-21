package com.palja.timedeal_service.application.event.internal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class TimeDealStockDecreaseEventReq {
    private final UUID timeDealId;
    private final UUID productId;
    private final long quantity;
}
