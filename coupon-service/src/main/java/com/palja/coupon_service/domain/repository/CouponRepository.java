package com.palja.coupon_service.domain.repository;

import com.palja.coupon_service.domain.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CouponRepository {

    Coupon save(Coupon coupon);

    Page<Coupon> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<Coupon> findByIdAndDeletedAtIsNull(UUID id);

    void delete(Coupon coupon);
}
