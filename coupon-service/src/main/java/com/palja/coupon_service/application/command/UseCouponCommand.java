package com.palja.coupon_service.application.command;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UseCouponCommand(
        UUID couponUserId,
        String userId,
        UUID orderId,
        Long discountAmount
) {
}
