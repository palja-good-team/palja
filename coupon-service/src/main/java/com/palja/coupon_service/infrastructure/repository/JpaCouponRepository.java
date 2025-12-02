package com.palja.coupon_service.infrastructure.repository;

import com.palja.coupon_service.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaCouponRepository extends JpaRepository<Coupon, UUID> {
}
