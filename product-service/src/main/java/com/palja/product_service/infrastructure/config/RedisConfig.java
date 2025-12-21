package com.palja.product_service.infrastructure.config;

import jakarta.annotation.PostConstruct;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.LongCodec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String hostName;
    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        LettuceClientConfiguration config = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(3))
                .shutdownTimeout(Duration.ofSeconds(5))
                .build();

        return new LettuceConnectionFactory(
                new RedisStandaloneConfiguration(hostName, port), config);
    }

    @Bean
    public RedissonClient redisson(RedisConnectionFactory factory) {

        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + hostName + ":" + port);
        //기본값. 락을 획득한 채로 스프링 서버가 죽으면, 30초후에 락이 자동으로 해제됨
        config.setLockWatchdogTimeout(30000);
        config.setCodec(new CompositeCodec(
                StringCodec.INSTANCE,
                LongCodec.INSTANCE,
                StringCodec.INSTANCE
        ));

        return Redisson.create(config);
    }

    @PostConstruct
    public void init() {
        System.out.println("host = "+hostName+", port = "+ port + ", password = "+password);
    }
}
