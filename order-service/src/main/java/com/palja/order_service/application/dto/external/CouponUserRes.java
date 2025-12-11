package com.palja.order_service.application.dto.external;

import com.palja.order_service.application.dto.CouponDiscountType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponUserRes {

    private UUID couponUserId;
    private final UUID couponId;
    private final String couponName;
    private final CouponDiscountType discountType;      // PERCENTAGE, FIXED 등
    private final Integer discountValue;
    private final BigDecimal maxDiscountAmount;
    private final BigDecimal minOrderAmount;
    private LocalDateTime expireAt;
    private final String status;            // ACTIVE, PAUSED, EXPIRED, DELETED
}