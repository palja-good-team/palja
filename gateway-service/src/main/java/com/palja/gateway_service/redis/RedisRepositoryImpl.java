package com.palja.gateway_service.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements TokenRepository {

	private final RedisTemplate<String, String> redisTemplate;

	public void save(String key, String value, long ttl) {
		redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MILLISECONDS);
	}

	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public void remove(String key) {
		redisTemplate.delete(key);
	}

}