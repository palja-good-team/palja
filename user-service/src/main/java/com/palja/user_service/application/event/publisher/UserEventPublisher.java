package com.palja.user_service.application.event.publisher;

import com.palja.user_service.application.event.dto.impl.DeleteCompanyUserEvent;
import com.palja.user_service.application.event.dto.impl.DeleteCustomerEvent;

public interface UserEventPublisher {

	void publishCustomerDeleteEvent(DeleteCustomerEvent event);

	void publishCompanyUserDeleteEvent(DeleteCompanyUserEvent event);

}
