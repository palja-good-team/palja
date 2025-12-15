package com.palja.order_service.application.dto.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderCreatedSagaEvent {

    private UUID orderId;

    public static OrderCreatedSagaEvent of(UUID orderId) {
        return OrderCreatedSagaEvent.builder()
                .orderId(orderId)
                .build();
    }
}