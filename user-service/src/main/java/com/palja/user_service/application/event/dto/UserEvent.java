package com.palja.user_service.application.event.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.palja.user_service.application.event.dto.request.DeleteCompanyUserEventReq;
import com.palja.user_service.application.event.dto.request.DeleteCustomerEventReq;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME,
	include = JsonTypeInfo.As.PROPERTY,
	property = "@type"
)
@JsonSubTypes({
	@JsonSubTypes.Type(value = DeleteCustomerEventReq.class, name = "DeleteCustomerEventReq"),
	@JsonSubTypes.Type(value = DeleteCompanyUserEventReq.class, name = "DeleteCompanyUserEventReq")
})
public interface UserEvent {
}
