package com.palja.coupon_service.application.dto;

import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.vo.CouponUserStatus;
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
    private LocalDateTime expireAt;
    private CouponUserStatus status;

    public static CouponUserRes from(CouponUser couponUser) {
        return CouponUserRes.builder()
                .couponUserId(couponUser.getId())
                .couponId(couponUser.getCouponId())
                .userId(couponUser.getUserId())
                .expireAt(couponUser.getExpireAt())
                .status(couponUser.getStatus())
                .build();
    }
}
