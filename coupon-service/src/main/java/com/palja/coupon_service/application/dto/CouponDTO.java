package com.palja.coupon_service.application.dto;

import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.vo.CouponStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CouponDTO {
    private String couponName;
    private String description;
    private DiscountType discountType;
    private int discountValue;
    private int totalQuantity;
    private int maxDiscountAmount;
    private int minOrderAmount;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private int validityDays;
    private CouponStatus status;

    public static CouponDTO from(Coupon coupon) {
        return CouponDTO.builder()
                .couponName(coupon.getName())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .totalQuantity(coupon.getTotalQuantity())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .minOrderAmount(coupon.getMinOrderAmount())
                .issueStartAt(coupon.getIssueStartAt())
                .issueEndAt(coupon.getIssueEndAt())
                .validityDays(coupon.getValidityDays())
                .status(coupon.getStatus())
                .build();
    }
}
