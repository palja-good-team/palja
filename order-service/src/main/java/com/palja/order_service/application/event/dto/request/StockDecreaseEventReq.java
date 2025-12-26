package com.palja.order_service.application.event.dto.request;

import com.palja.order_service.application.event.dto.SagaEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * 재고 차감 요청 이벤트
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class StockDecreaseEventReq implements SagaEvent {

    private final UUID sagaId;
    private final UUID orderId;

    private final UUID productId;
    private final UUID timeDealId;
    private final Long quantity;
    private final boolean isTimeDeal;

    public static StockDecreaseEventReq of(
            UUID sagaId,
            UUID orderId,
            UUID productId,
            UUID timeDealId,
            Long quantity,
            boolean isTimeDeal
    ) {
        return StockDecreaseEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .productId(productId)
                .timeDealId(timeDealId)
                .quantity(quantity)
                .isTimeDeal(isTimeDeal)
                .build();
    }
}
