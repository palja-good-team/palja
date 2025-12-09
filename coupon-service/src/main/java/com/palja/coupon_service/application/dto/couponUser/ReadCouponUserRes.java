package com.palja.coupon_service.application.dto.couponUser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.vo.CouponUserStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReadCouponUserRes {
    private UUID couponUserId;
    private UUID couponId;
    private String userId;
    private String couponName;
    private LocalDateTime expireAt;
    private CouponUserStatus status;
    private LocalDateTime usedAt;
    private UUID orderId;

    public static ReadCouponUserRes from(CouponUser couponUser){
        Coupon coupon = couponUser.getCoupon();

        return ReadCouponUserRes.builder()
                .couponUserId(couponUser.getId())
                .couponId(coupon.getId())
                .userId(couponUser.getUserId())
                .couponName(coupon.getName())
                .expireAt(couponUser.getExpireAt())
                .status(couponUser.getStatus())
                .usedAt(couponUser.getUsedAt())
                .orderId(couponUser.getOrderId())
                .build();
    }
}
