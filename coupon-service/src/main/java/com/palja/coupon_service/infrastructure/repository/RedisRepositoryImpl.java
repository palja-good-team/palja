package com.palja.coupon_service.infrastructure.repository;

import com.palja.coupon_service.domain.repository.RedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    private static final String COUPON_ISSUED_COUNT_KEY = "coupon:issued:count:";
    private static final String COUPON_LOCK_KEY_PREFIX = "lock:coupon:issue:";
    private static final String COUPON_ISSUED_USERS_KEY = "coupon:issued:users:";

    private static final int DurationDays = 7;

    @Override
    public void initIssuedCount(UUID couponId, Integer issuedQuantity) {
        String quantityKey = COUPON_ISSUED_COUNT_KEY + couponId;
        String usersKey = COUPON_ISSUED_USERS_KEY + couponId;

        if (redisTemplate.hasKey(quantityKey))
            return;

        redisTemplate.opsForValue().setIfAbsent(
                quantityKey,
                String.valueOf(issuedQuantity),
                Duration.ofDays(DurationDays)
        );

        if (!redisTemplate.hasKey(usersKey))
            redisTemplate.expire(usersKey, Duration.ofDays(DurationDays));

        log.info("Redis 쿠폰 발급 카운트 초기화 - couponId: {}, quantity: {}", couponId, issuedQuantity);
    }

    @Override
    public void issued(UUID couponId, String userId) {
        String issuedKey = COUPON_ISSUED_USERS_KEY + couponId;
        redisTemplate.opsForSet().add(issuedKey, userId);
    }

    @Override
    public void decreaseIssuedQuantity(UUID couponId) {
        String quantityKey = COUPON_ISSUED_COUNT_KEY + couponId;
        redisTemplate.opsForValue().decrement(quantityKey);
    }

    @Override
    public Long increaseIssuedQuantity(UUID couponId) {
        String quantityKey = COUPON_ISSUED_COUNT_KEY + couponId;
        return redisTemplate.opsForValue().increment(quantityKey);
    }

    @Override
    public void rollbackQuantity(UUID couponId, String userId) {
        try {
            decreaseIssuedQuantity(couponId);
            String usersKey = COUPON_ISSUED_USERS_KEY + couponId;
            redisTemplate.opsForSet().remove(usersKey, userId);
            log.info("Redis 쿠폰 재고 롤백 성공 - couponId: {}", couponId);
        } catch (Exception e) {
            log.error("Redis 쿠폰 재고 롤백 실패 - couponId: {}", couponId);
        }
    }

    @Override
    public boolean isDuplicated(UUID couponId, String userId) {
        String issuedKey = COUPON_ISSUED_USERS_KEY + couponId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(issuedKey, userId));
    }

    @Override
    public String getLockKey(UUID couponId) {
        return COUPON_LOCK_KEY_PREFIX + couponId;
    }

    @Override
    public RLock getLock(String lockKey) {
        return redissonClient.getLock(lockKey);
    }
}
