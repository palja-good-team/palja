package com.palja.payment_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.payment_service.application.event.dto.request.PaymentCancelEventReq;
import com.palja.payment_service.application.event.dto.request.PaymentCreateEventReq;
import com.palja.payment_service.application.event.dto.response.PaymentCreateEventRes;

@JsonSubTypes({
        @JsonSubTypes.Type(value = PaymentCreateEventReq.class, name = "PaymentCreateEventReq"),
        @JsonSubTypes.Type(value = PaymentCancelEventReq.class, name = "PaymentCancelEventReq"),

        @JsonSubTypes.Type(value = PaymentCreateEventRes.class, name = "PaymentCreateEventRes")
})
public interface OrderEvent extends KafkaEvent {
}
