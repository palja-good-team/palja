package com.palja.user_service.application.event.publisher;

import com.palja.user_service.application.event.dto.impl.DeleteCompanyUserEventReq;
import com.palja.user_service.application.event.dto.impl.DeleteCustomerEventReq;

public interface UserEventPublisher {

	void publishCustomerDeleteEvent(DeleteCustomerEventReq event);

	void publishCompanyUserDeleteEvent(DeleteCompanyUserEventReq event);

}
