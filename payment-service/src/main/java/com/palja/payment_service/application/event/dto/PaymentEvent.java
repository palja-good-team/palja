package com.palja.payment_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.palja.payment_service.application.event.dto.request.*;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PaymentApprovedEventReq.class, name = "PaymentApprovedEventReq"),
        @JsonSubTypes.Type(value = PaymentCanceledEventReq.class, name = "PaymentCanceledEventReq"),
        @JsonSubTypes.Type(value = PaymentFailedEventReq.class, name = "PaymentFailedEventReq"),
        @JsonSubTypes.Type(value = PaymentCancelFailedEventReq.class, name = "PaymentCancelFailedEventReq")
})
public interface PaymentEvent {
}
