package com.palja.order_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.order_service.application.event.dto.request.OrderCanceledEventReq;

@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderCanceledEventReq.class, name = "OrderCanceledEventReq"),
})
public interface OrderEvent extends KafkaEvent {
}