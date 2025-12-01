package com.palja.coupon_service.presentation.dto.response;

import com.palja.coupon_service.application.dto.CouponDTO;
import com.palja.coupon_service.domain.vo.CouponStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouponRes {
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

    public static CreateCouponRes from(CouponDTO couponDTO) {
        return CreateCouponRes.builder()
                .couponName(couponDTO.getCouponName())
                .description(couponDTO.getDescription())
                .discountType(couponDTO.getDiscountType())
                .discountValue(couponDTO.getDiscountValue())
                .totalQuantity(couponDTO.getTotalQuantity())
                .maxDiscountAmount(couponDTO.getMaxDiscountAmount())
                .minOrderAmount(couponDTO.getMinOrderAmount())
                .issueStartAt(couponDTO.getIssueStartAt())
                .issueEndAt(couponDTO.getIssueEndAt())
                .validityDays(couponDTO.getValidityDays())
                .status(couponDTO.getStatus())
                .build();
    }
}
