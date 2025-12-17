package com.palja.user_service.application.event.publisher;

import com.palja.user_service.application.event.dto.UserEvent;

public interface UserEventPublisher {

	void publish(UserEvent event);

}
