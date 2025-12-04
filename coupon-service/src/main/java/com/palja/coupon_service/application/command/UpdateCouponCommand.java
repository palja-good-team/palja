package com.palja.coupon_service.application.command;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UpdateCouponCommand(
        UUID couponId,
        String couponName,
        String description,
        Integer totalQuantity,
        Integer maxDiscountAmount,
        Integer minOrderAmount,
        LocalDateTime issueStartAt,
        LocalDateTime issueEndAt
) {
}