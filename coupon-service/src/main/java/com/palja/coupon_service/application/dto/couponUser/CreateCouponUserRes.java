package com.palja.coupon_service.application.dto.couponUser;

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
public class CreateCouponUserRes {
    private UUID couponUserId;
    private UUID couponId;
    private String userId;
    private String couponName;
    private DiscountType discountType;
    private Integer discountValue;
    private LocalDateTime expireAt;
    private CouponUserStatus status;
    private LocalDateTime createdAt;
    private String createdBy;

    public static CreateCouponUserRes from(CouponUser couponUser) {
        Coupon coupon = couponUser.getCoupon();

        return CreateCouponUserRes.builder()
                .couponUserId(couponUser.getId())
                .couponId(coupon.getId())
                .userId(couponUser.getUserId())
                .couponName(couponUser.getCoupon().getName())
                .discountType(coupon.getDiscountPolicy().getDiscountType())
                .discountValue(coupon.getDiscountPolicy().getDiscountValue())
                .expireAt(couponUser.getExpireAt())
                .status(couponUser.getStatus())
                .createdAt(couponUser.getCreatedAt())
                .createdBy(couponUser.getCreatedBy())
                .build();
    }
}
