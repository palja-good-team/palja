package com.palja.user_service.domain.repository;

public interface LoginQueueRepository {

	void enqueueLogin(String loginId, long currentTimeMillis);

	Long getQueueRank(String loginId);

	void deleteQueue(String loginId);

	void addWhiteList(String loginId, String queueToken);

	String getQueueToken(String loginId);

}
