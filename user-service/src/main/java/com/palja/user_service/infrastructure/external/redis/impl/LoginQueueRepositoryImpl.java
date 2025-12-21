package com.palja.user_service.infrastructure.external.redis.impl;

import org.springframework.stereotype.Component;

import com.palja.user_service.domain.repository.LoginQueueRepository;
import com.palja.user_service.infrastructure.external.redis.RedisRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginQueueRepositoryImpl implements LoginQueueRepository {

	private final RedisRepository redisRepository;

	private final String LOGIN_QUEUE_KEY = "AUTH:LQ";
	private final String MAX_CONCURRENT = "1";

	@Override
	public void enqueueLogin(String loginId, long currentTimeMillis) {
		redisRepository.zadd(LOGIN_QUEUE_KEY, loginId, currentTimeMillis);
	}

}
