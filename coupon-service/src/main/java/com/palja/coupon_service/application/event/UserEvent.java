package com.palja.coupon_service.application.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.coupon_service.application.event.dto.request.DeleteCustomerEventReq;

@JsonSubTypes({
        @JsonSubTypes.Type(value = DeleteCustomerEventReq.class, name = "DeleteCustomerEventReq")
})
public interface UserEvent extends KafkaEvent {
}
