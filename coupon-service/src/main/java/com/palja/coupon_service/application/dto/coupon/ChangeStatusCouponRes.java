package com.palja.coupon_service.application.dto.coupon;

import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.vo.CouponStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public class ChangeStatusCouponRes {
    private UUID couponId;
    private CouponStatus couponStatus;
    private LocalDateTime updateAt;
    private String updateBy;

    public static ChangeStatusCouponRes from(Coupon coupon) {
        return ChangeStatusCouponRes.builder()
                .couponId(coupon.getId())
                .couponStatus(coupon.getStatus())
                .updateAt(coupon.getUpdatedAt())
                .updateBy(coupon.getUpdatedBy())
                .build();
    }
}
