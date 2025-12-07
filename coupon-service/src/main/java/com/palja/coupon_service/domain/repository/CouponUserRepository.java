package com.palja.coupon_service.domain.repository;

import com.palja.coupon_service.domain.entity.CouponUser;

import java.util.UUID;

public interface CouponUserRepository {

    CouponUser save(CouponUser couponUser);

    boolean existsByCouponIdAndUserIdAndDeletedAtIsNull(UUID couponId, String userId);
}
