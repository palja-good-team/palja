package com.palja.order_service.application.event.dto.request;

import com.palja.order_service.application.event.dto.SagaEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * 재고 복구 요청 이벤트 (보상)
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class StockRestoreEventReq implements SagaEvent {

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

    // OrderCanceledEventReq 기반 재고 복구 이벤트 생성
    public static StockRestoreEventReq from(OrderCanceledEventReq canceledEvent) {
        return StockRestoreEventReq.builder()
                .sagaId(canceledEvent.getOrderId())
                .orderId(canceledEvent.getOrderId())
                .productId(canceledEvent.getProductId())
                .timeDealId(canceledEvent.getTimeDealId())
                .quantity(canceledEvent.getQuantity())
                .isTimeDeal(canceledEvent.getTimeDealId() != null)
                .build();
    }
}