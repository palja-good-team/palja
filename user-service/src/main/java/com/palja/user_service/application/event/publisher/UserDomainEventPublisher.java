package com.palja.user_service.application.event.publisher;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserDomainEventPublisher {

	private final ApplicationEventPublisher applicationEventPublisher;

	public void publishEvent(Object event) {
		applicationEventPublisher.publishEvent(event);
	}

}
