package com.palja.coupon_service.application.command;

import java.util.UUID;

public record IssueCouponCommand(
        UUID couponId,
        String userId
) {
}
