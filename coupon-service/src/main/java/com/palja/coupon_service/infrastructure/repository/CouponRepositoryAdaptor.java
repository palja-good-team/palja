package com.palja.coupon_service.infrastructure.repository;

import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CouponRepositoryAdaptor implements CouponRepository {

    private final JpaCouponRepository jpaCouponRepository;

    @Override
    public Coupon save(Coupon coupon) {
        return jpaCouponRepository.save(coupon);
    }

    @Override
    public Optional<Coupon> findById(UUID id) {
        return jpaCouponRepository.findById(id);
    }

    @Override
    public void delete(Coupon coupon) {
        jpaCouponRepository.delete(coupon);
    }
}
