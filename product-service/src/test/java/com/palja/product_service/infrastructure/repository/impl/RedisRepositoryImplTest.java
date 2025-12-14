package com.palja.product_service.infrastructure.repository.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMap;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisRepositoryImplTest {

    @Container
    private static final GenericContainer<?> redisContainer =
            new GenericContainer<>("redis:7.4.1-alpine3.20")
                    .withExposedPorts(6379)
                    .waitingFor(Wait.forListeningPort())
                    .withStartupTimeout(Duration.ofSeconds(60));

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    static {
        redisContainer.start();
    }

    @Autowired
    private RedisProductRepositoryImpl redisRepository;

    @Autowired
    private RedissonClient redissonClient;

    private final String hashKey = "test:stock";

    @AfterEach
    void clean() {
        redissonClient.getMap(hashKey).clear();
    }

    @Test
    @DisplayName("판매에 의한 재고 차감시, 동시성 이슈가 없어야한다")
    void decreaseStockBySale() throws InterruptedException {
        //given
        String hashName = hashKey;
        String productId = UUID.randomUUID().toString();
        Integer dbStock = 100;
        Integer saleQuantity = 1;
        long beforeTime = LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC);

        final int numOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
        CountDownLatch latch = new CountDownLatch(numOfThreads);

        //when
        for (int i = 1; i <= numOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    redisRepository.decreaseStockBySale(productId, dbStock, saleQuantity);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        long afterTime = LocalDateTime.now().plusMinutes(1).toEpochSecond(ZoneOffset.UTC);

        //then
        RMap<String, Integer> map = redissonClient.getMap(hashName);
        RScoredSortedSet<String> timeSet = redissonClient.getScoredSortedSet(hashName + "Time");

        assertThat(map.get(productId)).isEqualTo(0);
        assertThat(timeSet.size()).isEqualTo(1);

        assertThat(timeSet.getScore(productId)).isBetween(beforeTime * 1.0, afterTime * 1.0);
    }

    @Test
    @DisplayName("재고 수량 변경에 성공한다")
    void adjustStock() {
        //given
        String hashName = hashKey;
        String productId = UUID.randomUUID().toString();
        String productId2 = UUID.randomUUID().toString();
        Integer beforeStock = 100;
        Integer afterStock = 200;

        redissonClient.getMap(hashName).fastPut(productId, beforeStock);

        //when

        redisRepository.adjustStock(productId, afterStock);
        redisRepository.adjustStock(productId2, afterStock);

        //then
        RMap<String, Integer> map = redissonClient.getMap(hashName);

        assertThat(map.size()).isEqualTo(2);
        assertThat(map.get(productId)).isEqualTo(afterStock);
        assertThat(map.get(productId2)).isEqualTo(afterStock);
    }
}