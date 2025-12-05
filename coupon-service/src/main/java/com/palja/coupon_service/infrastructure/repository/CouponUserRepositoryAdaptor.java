package com.palja.coupon_service.infrastructure.repository;

import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.repository.CouponUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CouponUserRepositoryAdaptor implements CouponUserRepository {

    private final JpaCouponUserRepository jpaCouponUserRepository;

    @Override
    public CouponUser save(CouponUser couponUser) {
        return jpaCouponUserRepository.save(couponUser);
    }

    @Override
    public boolean existsByCouponIdAndUserId(UUID couponId, String userId) {
        return jpaCouponUserRepository.existsByCouponIdAndUserId(couponId, userId);
    }
}
