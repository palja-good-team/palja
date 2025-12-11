package com.palja.coupon_service.application.dto.couponUser;

import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.vo.CouponUserStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ChangeStatusCouponUserRes {
    private UUID couponUserId;
    private String userId;
    private CouponUserStatus couponUserStatus;
    private LocalDateTime updateAt;
    private String updateBy;

    public static ChangeStatusCouponUserRes from(CouponUser couponUser) {
        return ChangeStatusCouponUserRes.builder()
                .couponUserId(couponUser.getId())
                .userId(couponUser.getUserId())
                .couponUserStatus(couponUser.getStatus())
                .updateAt(couponUser.getUsedAt())
                .updateBy(couponUser.getUpdatedBy())
                .build();
    }
}
