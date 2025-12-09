package com.palja.coupon_service.application.dto.couponUser;

import com.palja.coupon_service.domain.entity.CouponUser;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public class DeleteCouponUserRes {
    private UUID couponUserId;
    private String userId;
    private LocalDateTime deleteAt;
    private String deleteBy;

    public static DeleteCouponUserRes from(CouponUser couponUser) {
        return DeleteCouponUserRes.builder()
                .couponUserId(couponUser.getId())
                .userId(couponUser.getUserId())
                .deleteAt(couponUser.getDeletedAt())
                .deleteBy(couponUser.getDeletedBy())
                .build();
    }
}
