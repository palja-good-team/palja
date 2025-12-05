package com.palja.coupon_service.application.dto;

import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.vo.CouponStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CouponRes {
    private UUID couponId;
    private String couponName;
    private String description;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer totalQuantity;
    private Integer maxDiscountAmount;
    private Integer minOrderAmount;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private Integer usageDays;
    private CouponStatus status;

    public static CouponRes from(Coupon coupon) {
        return CouponRes.builder()
                .couponId(coupon.getId())
                .couponName(coupon.getName())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountPolicy().getDiscountType())
                .discountValue(coupon.getDiscountPolicy().getDiscountValue())
                .totalQuantity(coupon.getTotalQuantity())
                .maxDiscountAmount(coupon.getAmountPolicy().getMaxDiscountAmount())
                .minOrderAmount(coupon.getAmountPolicy().getMinOrderAmount())
                .issueStartAt(coupon.getIssuePeriod().getIssueStartAt())
                .issueEndAt(coupon.getIssuePeriod().getIssueEndAt())
                .usageDays(coupon.getUsageDays())
                .status(coupon.getStatus())
                .build();
    }
}
