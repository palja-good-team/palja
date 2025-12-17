package com.palja.order_service.application.dto.event.request;

import lombok.*;

import java.util.UUID;

/**
 * Saga 시작 요청 이벤트
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class SagaStartEventReq {

    private final UUID sagaId;
    private final UUID orderId;

    @Builder.Default
    private final long timestamp = System.currentTimeMillis();

    public static SagaStartEventReq of(UUID sagaId, UUID orderId) {
        return SagaStartEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .build();
    }
}
