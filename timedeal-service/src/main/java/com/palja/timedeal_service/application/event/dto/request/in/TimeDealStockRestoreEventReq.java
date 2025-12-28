package com.palja.timedeal_service.application.event.dto.request.in;

import com.palja.timedeal_service.application.event.dto.OrderEvent;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TimeDealStockRestoreEventReq implements OrderEvent {

    private UUID sagaId;
    private UUID orderId;

    private UUID productId;
    private UUID timeDealId;
    private Long quantity;

    private boolean isTimeDeal;
}