package com.palja.order_service.application.event.dto.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEventReq {
    private UUID orderId;
    private UUID sagaId;

    public static OrderCreatedEventReq of(UUID orderId, UUID sagaId) {
        return OrderCreatedEventReq.builder()
                .orderId(orderId)
                .sagaId(sagaId)
                .build();
    }
}