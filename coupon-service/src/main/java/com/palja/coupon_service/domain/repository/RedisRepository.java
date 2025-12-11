package com.palja.coupon_service.domain.repository;

import org.redisson.api.RLock;

import java.util.UUID;

public interface RedisRepository {

    void initIssuedCount(UUID couponId, Integer issuedQuantity);

    void issued(UUID couponId, String userId);

    void decreaseIssuedQuantity(UUID couponId);

    Long increaseIssuedQuantity(UUID couponId);

    void rollbackQuantity(UUID couponId, String userId);

    boolean isDuplicated(UUID couponId, String userId);

    String getLockKey(UUID couponId);

    RLock getLock(String lockKey);

}
