package com.palja.coupon_service.domain.repository;

import com.palja.coupon_service.application.dto.CouponUserRes;
import com.palja.coupon_service.domain.entity.CouponUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CouponUserRepository {

    CouponUser save(CouponUser couponUser);

    boolean existsByCouponIdAndUserIdAndDeletedAtIsNull(UUID couponId, String userId);

    Page<CouponUser> findAllByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);
}
