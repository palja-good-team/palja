package com.palja.coupon_service.application.dto.couponUser;

import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.vo.CouponUserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UsedCouponUserRes {
    private UUID couponUserId;
    private UUID couponId;
    private String userId;
    private String couponName;
    private CouponUserStatus status;
    private LocalDateTime usedAt;
    private UUID orderId;
    private Long discountAmount;

    public static UsedCouponUserRes from(CouponUser couponUser) {
        Coupon coupon = couponUser.getCoupon();

        return UsedCouponUserRes.builder()
                .couponUserId(couponUser.getId())
                .couponId(coupon.getId())
                .userId(couponUser.getUserId())
                .couponName(coupon.getName())
                .status(couponUser.getStatus())
                .usedAt(couponUser.getUsedAt())
                .orderId(couponUser.getOrderId())
                .discountAmount(couponUser.getDiscountAmount())
                .build();
    }
}
