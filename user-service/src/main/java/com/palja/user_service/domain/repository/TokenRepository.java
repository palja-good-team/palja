package com.palja.user_service.domain.repository;

public interface TokenRepository {

	void addAccessTokenToBlackList(String loginId, String value, long ttl);

	void addRefreshTokenToWhiteList(String loginId, String value, long ttl);

	String getRefreshToken(String loginId);

	void deleteRefreshToken(String loginId);

}
