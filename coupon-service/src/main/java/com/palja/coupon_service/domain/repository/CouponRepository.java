package com.palja.coupon_service.domain.repository;

import com.palja.coupon_service.domain.entity.Coupon;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(UUID id);

    void delete(Coupon coupon);
}
