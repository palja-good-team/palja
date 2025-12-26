package com.palja.payment_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = OrderEvent.class, name = "OrderEvent"),
        @JsonSubTypes.Type(value = PaymentEvent.class, name = "PaymentEvent")
})
public interface KafkaEvent {
}
