package com.palja.user_service.application.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.palja.user_service.application.event.dto.impl.DeleteCompanyUserEvent;
import com.palja.user_service.application.event.dto.impl.DeleteCustomerEvent;
import com.palja.user_service.application.event.publisher.UserEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDomainEventListener {

	private final UserEventPublisher userEventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleCustomerDeleted(DeleteCustomerEvent event) {
		userEventPublisher.publish(event);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleCompanyUserDeleted(DeleteCompanyUserEvent event) {
		userEventPublisher.publish(event);
	}

}
