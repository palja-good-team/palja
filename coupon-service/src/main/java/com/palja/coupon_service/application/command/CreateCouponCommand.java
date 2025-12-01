package com.palja.coupon_service.application.command;

import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreateCouponCommand(
        String couponName,
        String description,
        DiscountType discountType,
        Integer discountValue,
        Integer totalQuantity,
        Integer maxDiscountAmount,
        Integer minOrderAmount,
        LocalDateTime issueStartAt,
        LocalDateTime issueEndAt,
        Integer validityDays
) {
}
