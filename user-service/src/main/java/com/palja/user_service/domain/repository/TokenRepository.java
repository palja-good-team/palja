package com.palja.user_service.domain.repository;

public interface TokenRepository {

	void save(String key, String value, long ttl);

	String get(String key);

	void remove(String key);

}
