package com.palja.order_service.infrastructure.external.dto.response;

import com.palja.order_service.application.dto.CouponDiscountType;
import com.palja.order_service.application.dto.external.CouponUserRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponUserDetailDTO {

    private UUID couponUserId;
    private UUID couponId;
    private String userId;
    private String couponName;
    private String couponDescription;
    private CouponDiscountType discountType;      // PERCENTAGE, FIXED
    private int discountValue;
    private Integer maxDiscountAmount;
    private Integer minOrderAmount;
    private LocalDateTime expireAt;
    private String status;              // ACTIVE, PAUSED, EXPIRED
    private LocalDateTime usedAt;
    private UUID orderId;
    private Long discountAmount;

    public CouponUserRes toResponse() {
        return CouponUserRes.builder()
                .couponUserId(couponUserId)
                .couponId(couponId)
                .couponName(couponName)
                .discountType(discountType)
                .discountValue(discountValue)
                .maxDiscountAmount(maxDiscountAmount != null ? BigDecimal.valueOf(maxDiscountAmount) : null)
                .minOrderAmount(minOrderAmount != null ? BigDecimal.valueOf(minOrderAmount) : null)
                .expireAt(expireAt)
                .status(status)
                .build();
    }
}