package com.palja.product_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.palja.product_service.infrastructure.external.kafka.dto.DeleteAllByUserDto;

@JsonSubTypes({
        @JsonSubTypes.Type(value = DeleteAllByUserDto.class, name = "DeleteCompanyUserEventReq")
})
public interface UserEvent extends KafkaEvent{
}
