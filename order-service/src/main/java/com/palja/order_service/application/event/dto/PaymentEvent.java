package com.palja.order_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.order_service.application.event.dto.response.PaymentBaseEventRes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = PaymentBaseEventRes.class, name = "PaymentBaseEventRes"),
})
public interface PaymentEvent extends KafkaEvent {
}