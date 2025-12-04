package com.palja.gateway_service.redis;

public interface TokenRepository {

	void save(String key, String value, long ttl);

	String get(String key);

	void remove(String key);

}
