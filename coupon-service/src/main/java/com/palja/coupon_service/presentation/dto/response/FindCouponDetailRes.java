package com.palja.coupon_service.presentation.dto.response;

import com.palja.coupon_service.application.dto.CouponDetailRes;
import com.palja.coupon_service.domain.vo.CouponStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindCouponDetailRes {
    private UUID couponId;
    private String couponName;
    private String description;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer totalQuantity;
    private Integer issuedQuantity;
    private Integer maxDiscountAmount;
    private Integer minOrderAmount;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private CouponStatus status;
    private Instant createdAt;
    private String createdBy;
    private Instant updatedAt;
    private String updatedBy;

    public static FindCouponDetailRes from(CouponDetailRes couponDetailRes) {
        return FindCouponDetailRes.builder()
                .couponId(couponDetailRes.getCouponId())
                .couponName(couponDetailRes.getCouponName())
                .description(couponDetailRes.getDescription())
                .discountType(couponDetailRes.getDiscountType())
                .discountValue(couponDetailRes.getDiscountValue())
                .totalQuantity(couponDetailRes.getTotalQuantity())
                .issuedQuantity(couponDetailRes.getIssuedQuantity())
                .maxDiscountAmount(couponDetailRes.getMaxDiscountAmount())
                .minOrderAmount(couponDetailRes.getMinOrderAmount())
                .issueStartAt(couponDetailRes.getIssueStartAt())
                .issueEndAt(couponDetailRes.getIssueEndAt())
                .status(couponDetailRes.getStatus())
                .createdAt(couponDetailRes.getCreatedAt())
                .createdBy(couponDetailRes.getCreatedBy())
                .updatedAt(couponDetailRes.getUpdatedAt())
                .updatedBy(couponDetailRes.getUpdatedBy())
                .build();
    }
}
