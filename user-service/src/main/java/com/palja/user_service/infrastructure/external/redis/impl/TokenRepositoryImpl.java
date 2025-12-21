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

	private final String ACCESS_TOKEN_BLACKLIST_PREFIX = "AUTH:BL:AT:";
	private final String REFRESH_TOKEN_WHITELIST_PREFIX = "AUTH:WL:RT:";

	@Override
	public void addAccessTokenToBlackList(String loginId, String value, long ttl) {
		redisRepository.set(ACCESS_TOKEN_BLACKLIST_PREFIX + loginId, value, ttl, TimeUnit.MILLISECONDS);
	}

	@Override
	public void addRefreshTokenToWhiteList(String loginId, String value, long ttl) {
		redisRepository.set(REFRESH_TOKEN_WHITELIST_PREFIX + loginId, value, ttl, TimeUnit.MILLISECONDS);
	}

	@Override
	public String getRefreshToken(String loginId) {
		return redisRepository.get(REFRESH_TOKEN_WHITELIST_PREFIX + loginId);
	}

	@Override
	public void deleteRefreshToken(String loginId) {
		redisRepository.del(REFRESH_TOKEN_WHITELIST_PREFIX + loginId);
	}

}
