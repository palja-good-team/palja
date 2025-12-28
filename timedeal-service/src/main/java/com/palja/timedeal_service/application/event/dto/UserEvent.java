package com.palja.timedeal_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.timedeal_service.application.event.dto.request.in.TimeDealDeleteByCompanyUserEventReq;

@JsonSubTypes({
        @JsonSubTypes.Type(value = TimeDealDeleteByCompanyUserEventReq.class, name = "DeleteCompanyUserEventReq"),
})
public interface UserEvent extends KafkaEvent {
}
