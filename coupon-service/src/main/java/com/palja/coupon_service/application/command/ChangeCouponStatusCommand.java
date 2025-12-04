package com.palja.coupon_service.application.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ChangeCouponStatusCommand(
        UUID couponId,
        String status
) {
}
