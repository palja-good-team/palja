package com.palja.user_service.domain.repository;

public interface LoginQueueRepository {

	void enqueueLogin(String loginId, long currentTimeMillis);

}
