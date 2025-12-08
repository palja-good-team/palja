package com.palja.order_service.application.dto.response;

import com.palja.order_service.application.dto.CouponDiscountType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponRes {

    private final UUID couponId;
    private final String name;
    private final CouponDiscountType discountType;      // PERCENTAGE, FIXED 등
    private final int discountValue;
    private final BigDecimal maxDiscountAmount;
    private final BigDecimal minOrderAmount;
    private final LocalDateTime issueStartAt;
    private final LocalDateTime issueEndAt;
    private final Integer validityDays;
    private final String status;            // ACTIVE, PAUSED, EXPIRED, DELETED

    public static CouponRes of(
            UUID couponId,
            String name,
            CouponDiscountType discountType,
            int discountValue,
            BigDecimal maxDiscountAmount,
            BigDecimal minOrderAmount,
            LocalDateTime issueStartAt,
            LocalDateTime issueEndAt,
            Integer validityDays,
            String status
    ) {
        return CouponRes.builder()
                .couponId(couponId)
                .name(name)
                .discountType(discountType)
                .discountValue(discountValue)
                .maxDiscountAmount(maxDiscountAmount)
                .minOrderAmount(minOrderAmount)
                .issueStartAt(issueStartAt)
                .issueEndAt(issueEndAt)
                .validityDays(validityDays)
                .status(status)
                .build();
    }
}