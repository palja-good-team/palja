package com.palja.user_service.application.port;

import com.palja.user_service.application.event.dto.request.DeleteCompanyUserEventReq;
import com.palja.user_service.application.event.dto.request.DeleteCustomerEventReq;

public interface UserEventPublisher {

	void publishCustomerDeleteEvent(DeleteCustomerEventReq event);

	void publishCompanyUserDeleteEvent(DeleteCompanyUserEventReq event);

}
