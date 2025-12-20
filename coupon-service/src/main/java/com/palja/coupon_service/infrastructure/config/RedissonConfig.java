package com.palja.coupon_service.infrastructure.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(host + ":" + port) // Redis 주소
                .setConnectionMinimumIdleSize(10)           // 최소 유휴 연결 수
                .setConnectionPoolSize(20)                  // 최대 연결 수
                .setTimeout(3000)                           // Redis 명령 실행 제한 시간 (ms)
                .setConnectTimeout(10000)                   // Redis 서버 연결 제한 시간 (ms)
                .setIdleConnectionTimeout(10000)            // 유휴 연결 종류 시간 (ms)
                .setRetryAttempts(3)                        // 재시도 횟수
                .setRetryInterval(1500);                    // 재시도 간격 (ms)

        return Redisson.create(config);
    }
}
