package com.palja.coupon_service.application.command;

import com.palja.coupon_service.domain.vo.CouponStatus;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ChangeCouponStatusCommand(
        UUID couponId,
        CouponStatus status
) {
}
