package com.palja.coupon_service.application.dto.couponUser;

import com.palja.coupon_service.domain.entity.CouponUser;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class DeleteCouponUserRes {
    private UUID couponUserId;
    private String userId;
    private LocalDateTime deletedAt;
    private String deletedBy;

    public static DeleteCouponUserRes from(CouponUser couponUser) {
        return DeleteCouponUserRes.builder()
                .couponUserId(couponUser.getId())
                .userId(couponUser.getUserId())
                .deletedAt(couponUser.getDeletedAt())
                .deletedBy(couponUser.getDeletedBy())
                .build();
    }
}
