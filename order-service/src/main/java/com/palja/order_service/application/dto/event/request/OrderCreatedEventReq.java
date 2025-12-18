package com.palja.order_service.application.dto.event.request;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEventReq {
    private UUID orderId;
    private UUID sagaId;
}