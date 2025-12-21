package com.palja.timedeal_service.application.event.internal;

import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeDealStockDecreaseEventReq {
    private UUID timeDealId;
    private UUID productId;
    private long quantity;
}
