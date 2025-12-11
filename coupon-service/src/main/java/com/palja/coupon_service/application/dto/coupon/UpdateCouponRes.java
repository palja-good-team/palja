package com.palja.coupon_service.application.dto.coupon;

import com.palja.coupon_service.domain.entity.Coupon;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class UpdateCouponRes {
    private UUID couponId;
    private String name;
    private String description;
    private Integer totalQuantity;
    private Integer maxDiscountAmount;
    private Integer minOrderAmount;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private Integer usageDays;
    private LocalDateTime updateAt;
    private String updateBy;

    public static UpdateCouponRes from(Coupon coupon) {
        return UpdateCouponRes.builder()
                .couponId(coupon.getId())
                .name(coupon.getName())
                .description(coupon.getDescription())
                .totalQuantity(coupon.getTotalQuantity())
                .maxDiscountAmount(coupon.getAmountPolicy().getMaxDiscountAmount())
                .minOrderAmount(coupon.getAmountPolicy().getMinOrderAmount())
                .issueStartAt(coupon.getIssuePeriod().getIssueStartAt())
                .issueEndAt(coupon.getIssuePeriod().getIssueEndAt())
                .usageDays(coupon.getUsageDays())
                .updateAt(coupon.getUpdatedAt())
                .updateBy(coupon.getUpdatedBy())
                .build();
    }
}
