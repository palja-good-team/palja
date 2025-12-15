package com.palja.user_service.infrastructure.config;

import static org.springframework.data.redis.serializer.RedisSerializationContext.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.palja.user_service.infrastructure.util.CacheType;

@EnableCaching
@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	String host;

	@Value("${spring.data.redis.port}")
	int port;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(host, port);
		LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
			.commandTimeout(Duration.ofSeconds(5))
			.build();
		return new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		return redisTemplate;
	}

	@Bean
	public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
		for (CacheType cache : CacheType.values()) {
			redisCacheConfigurationMap.put(
				cache.getCacheName(), RedisCacheConfiguration.defaultCacheConfig()
					.disableCachingNullValues()
					.entryTtl(Duration.ofMinutes(30))
					.serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
					.serializeValuesWith(SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, cache.getValueType())))
			);
		}

		return RedisCacheManager.builder(redisConnectionFactory)
			.withInitialCacheConfigurations(redisCacheConfigurationMap)
			.build();
	}

}
