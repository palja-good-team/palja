package com.palja.order_service.application.dto.event.request;

import lombok.*;

import java.util.UUID;

/**
 * 재고 복구 요청 이벤트 (보상)
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class StockRestoreEventReq {

    private final UUID sagaId;
    private final UUID orderId;

    private final UUID productId;
    private final UUID timeDealId;
    private final Long quantity;
    private final boolean isTimeDeal;

    public static StockRestoreEventReq of(
            UUID sagaId,
            UUID orderId,
            UUID productId,
            UUID timeDealId,
            Long quantity,
            boolean isTimeDeal
    ) {
        return StockRestoreEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .productId(productId)
                .timeDealId(timeDealId)
                .quantity(quantity)
                .isTimeDeal(isTimeDeal)
                .build();
    }
}