package com.palja.order_service.application.dto.event.request;

import lombok.*;

import java.util.UUID;

/**
 * 재고 차감 요청 이벤트
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class StockDeductEventReq {

    private final UUID sagaId;
    private final UUID orderId;

    private final UUID productId;
    private final UUID timeDealId;
    private final Long quantity;
    private final boolean isTimeDeal;

    @Builder.Default
    private final UUID correlationId = UUID.randomUUID();
    @Builder.Default
    private final long timestamp = System.currentTimeMillis();

    public static StockDeductEventReq of(
            UUID sagaId,
            UUID orderId,
            UUID productId,
            UUID timeDealId,
            Long quantity,
            boolean isTimeDeal
    ) {
        return StockDeductEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .productId(productId)
                .timeDealId(timeDealId)
                .quantity(quantity)
                .isTimeDeal(isTimeDeal)
                .build();
    }
}
