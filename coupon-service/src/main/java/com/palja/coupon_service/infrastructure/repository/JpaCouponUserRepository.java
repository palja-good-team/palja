package com.palja.coupon_service.infrastructure.repository;

import com.palja.coupon_service.domain.entity.CouponUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaCouponUserRepository extends JpaRepository<CouponUser, UUID> {

    boolean existsByCouponIdAndUserId(UUID couponId, String userId);
}
