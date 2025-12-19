package com.palja.user_service.application.event.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.palja.user_service.application.event.dto.impl.DeleteCompanyUserEventReq;
import com.palja.user_service.application.event.dto.impl.DeleteCustomerEventReq;
import com.palja.user_service.application.event.publisher.UserEventPublisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDomainEventListener {

	private final UserEventPublisher userEventPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleCustomerDeleted(DeleteCustomerEventReq event) {
		userEventPublisher.publishCustomerDeleteEvent(event);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleCompanyUserDeleted(DeleteCompanyUserEventReq event) {
		userEventPublisher.publishCompanyUserDeleteEvent(event);
	}

}
