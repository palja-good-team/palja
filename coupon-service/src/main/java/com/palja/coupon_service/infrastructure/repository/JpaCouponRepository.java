package com.palja.coupon_service.infrastructure.repository;

import com.palja.coupon_service.domain.entity.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaCouponRepository extends JpaRepository<Coupon, UUID> {
    Page<Coupon> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<Coupon> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByNameAndDeletedAtIsNull(String couponName);
}
