package com.palja.coupon_service.application.dto.coupon;

import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.vo.CouponStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public class ReadCouponRes {
    private UUID couponId;
    private String couponName;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer totalQuantity;
    private Integer issuedQuantity;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private Integer usageDays;
    private CouponStatus status;

    public static ReadCouponRes from(Coupon coupon) {
        return ReadCouponRes.builder()
                .couponId(coupon.getId())
                .couponName(coupon.getName())
                .discountType(coupon.getDiscountPolicy().getDiscountType())
                .discountValue(coupon.getDiscountPolicy().getDiscountValue())
                .totalQuantity(coupon.getTotalQuantity())
                .issuedQuantity(coupon.getIssuedQuantity())
                .issueStartAt(coupon.getIssuePeriod().getIssueStartAt())
                .issueEndAt(coupon.getIssuePeriod().getIssueEndAt())
                .usageDays(coupon.getUsageDays())
                .status(coupon.getStatus())
                .build();
    }
}
