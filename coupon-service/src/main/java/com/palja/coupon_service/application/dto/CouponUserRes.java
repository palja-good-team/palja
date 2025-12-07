package com.palja.coupon_service.application.dto;

import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.vo.CouponUserStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CouponUserRes {
    private UUID couponUserId;
    private UUID couponId;
    private String userId;
    private String couponName;
    private DiscountType discountType;
    private Integer discountValue;
    private LocalDateTime expireAt;
    private CouponUserStatus status;

    public static CouponUserRes from(CouponUser couponUser) {
        Coupon coupon = couponUser.getCoupon();

        return CouponUserRes.builder()
                .couponUserId(couponUser.getId())
                .couponId(coupon.getId())
                .userId(couponUser.getUserId())
                .couponName(couponUser.getCoupon().getName())
                .discountType(coupon.getDiscountPolicy().getDiscountType())
                .discountValue(coupon.getDiscountPolicy().getDiscountValue())
                .expireAt(couponUser.getExpireAt())
                .status(couponUser.getStatus())
                .build();
    }
}
