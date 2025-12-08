package com.palja.product_service.infrastructure.event;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RedisTimeSetCleanListener {

    private final RedissonClient redissonClient;

    @Value("${redis-key.map0}")
    private String map0Key;

    @Value("${redis-key.map1}")
    private String map1Key;

    @Value("${redis-key.time-suffix}")
    private String timeSuffix;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterScheduleDbUpdate(RedisTimeSetClearEvent event) {

        redissonClient.getScoredSortedSet(map0Key +  timeSuffix).clear();
        redissonClient.getScoredSortedSet(map1Key +  timeSuffix).clear();
    }
}
