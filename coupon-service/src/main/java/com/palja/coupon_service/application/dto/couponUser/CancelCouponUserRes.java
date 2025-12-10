package com.palja.coupon_service.application.dto.couponUser;

import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.vo.CouponUserStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CancelCouponUserRes {
    private UUID couponUserId;
    private String userId;
    private CouponUserStatus status;

    public static CancelCouponUserRes from(CouponUser couponUser){
        return CancelCouponUserRes.builder()
                .couponUserId(couponUser.getId())
                .userId(couponUser.getUserId())
                .status(couponUser.getStatus())
                .build();
    }
}
