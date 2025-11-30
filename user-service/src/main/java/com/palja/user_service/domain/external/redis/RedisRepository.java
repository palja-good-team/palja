package com.palja.user_service.domain.external.redis;

public interface RedisRepository {

	void save(String key, String value, long ttl);

	String get(String key);

	void remove(String key);

}
