package com.palja.coupon_service.infrastructure.repository;

import com.palja.coupon_service.domain.entity.CouponUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCouponUserRepository extends JpaRepository<CouponUser, UUID> {

    boolean existsByCoupon_IdAndUserIdAndDeletedAtIsNull(UUID couponId, String userId);

    @Query("SELECT cu FROM CouponUser cu " +
            "JOIN FETCH cu.coupon c " +
            "WHERE cu.userId = :userId " +
            "AND cu.deletedAt IS NULL " +
            "ORDER BY cu.expireAt")
    Page<CouponUser> findAllByUserIdAndDeletedAtIsNull(String userId, Pageable pageable);

    @Query("SELECT cu FROM CouponUser cu " +
            "JOIN FETCH cu.coupon c " +
            "WHERE cu.id = :couponUserId " +
            "AND cu.userId = :userId " +
            "AND cu.deletedAt IS NULL " +
            "ORDER BY cu.expireAt")
    Optional<CouponUser> findByIdAndUserIdAndDeletedAtIsNull(UUID couponUserId, String userId);

    List<CouponUser> findAllByUserIdAndDeletedAtIsNull(String userId);
}
