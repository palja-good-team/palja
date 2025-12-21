package com.palja.order_service.application.event.dto.request;

import lombok.*;

import java.util.UUID;

/**
 * Saga 시작 요청 이벤트
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SagaStartEventReq {

    private UUID sagaId;
    private UUID orderId;

    public static SagaStartEventReq of(UUID sagaId, UUID orderId) {
        return SagaStartEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .build();
    }
}
