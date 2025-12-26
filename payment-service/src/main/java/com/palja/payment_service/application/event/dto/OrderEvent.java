package com.palja.payment_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.payment_service.application.event.dto.request.PaymentCancelEventReq;
import com.palja.payment_service.application.event.dto.request.PaymentCreateEventReq;

@JsonSubTypes({
        @JsonSubTypes.Type(value = PaymentCreateEventReq.class, name = "PaymentCreateEventReq"),
        @JsonSubTypes.Type(value = PaymentCancelEventReq.class, name = "PaymentCancelEventReq")
})
public interface OrderEvent extends KafkaEvent {
}
