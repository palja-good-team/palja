package com.palja.coupon_service.infrastructure.repository;

import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<Coupon> findAllByDeletedAtIsNull(Pageable pageable) {
        return jpaCouponRepository.findAllByDeletedAtIsNull(pageable);
    }

    @Override
    public Optional<Coupon> findByIdAndDeletedAtIsNull(UUID id) {
        return jpaCouponRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public void delete(Coupon coupon) {
        jpaCouponRepository.delete(coupon);
    }
}
