package com.palja.coupon_service.domain.repository;

import com.palja.coupon_service.domain.entity.CouponUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponUserRepository {

    CouponUser save(CouponUser couponUser);

    boolean existsByCoupon_IdAndUserIdAndDeletedAtIsNull(UUID couponId, String userId);

    Page<CouponUser> findAllByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);

    Optional<CouponUser> findByIdAndUserIdAndDeletedAtIsNull(UUID couponUserId, String userId);

    List<CouponUser> findAllByUserIdAndDeletedAtIsNull(String userId);
}
