package com.palja.user_service.infrastructure.external.redis.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.palja.user_service.domain.repository.TokenRepository;
import com.palja.user_service.infrastructure.external.redis.RedisRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenRepositoryImpl implements TokenRepository {

	private final RedisRepository redisRepository;

	@Override
	public void save(String key, String value, long ttl) {
		redisRepository.set(key, value, ttl, TimeUnit.MILLISECONDS);
	}

	@Override
	public String get(String key) {
		return redisRepository.get(key);
	}

	@Override
	public void remove(String key) {
		redisRepository.del(key);
	}

}
