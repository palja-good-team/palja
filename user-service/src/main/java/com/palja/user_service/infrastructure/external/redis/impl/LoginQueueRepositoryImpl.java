package com.palja.user_service.infrastructure.external.redis.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.palja.user_service.domain.repository.LoginQueueRepository;
import com.palja.user_service.infrastructure.external.redis.RedisRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginQueueRepositoryImpl implements LoginQueueRepository {

	private final RedisRepository redisRepository;

	private final String LOGIN_QUEUE_KEY = "AUTH:LQ";
	private final String QUEUE_RANK_KEY_PREFIX = "AUTH:WL:QT:";

	public final static Long MAX_CONCURRENT = 2L;

	@Override
	public void enqueueLogin(String loginId, long currentTimeMillis) {
		redisRepository.zadd(LOGIN_QUEUE_KEY, loginId, currentTimeMillis);
	}

	@Override
	public Long getQueueRank(String loginId) {
		return redisRepository.zrank(LOGIN_QUEUE_KEY, loginId);
	}

	@Override
	public void deleteQueue(String loginId) {
		redisRepository.zrem(LOGIN_QUEUE_KEY, loginId);
	}

	@Override
	public void addWhiteList(String loginId, String queueToken) {
		redisRepository.set(QUEUE_RANK_KEY_PREFIX + loginId, queueToken, 3, TimeUnit.MINUTES);
	}

}
