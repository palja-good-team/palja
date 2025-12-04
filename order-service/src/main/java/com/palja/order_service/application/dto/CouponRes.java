package com.palja.order_service.application.dto;

import com.palja.order_service.infrastructure.external.dto.response.CouponDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// 쿠폰 정보
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponRes {

    private final UUID couponId;
    private final String name;
    private final String discountType;      // PERCENTAGE, FIXED 등
    private final int discountValue;
    private final BigDecimal maxDiscountAmount;
    private final BigDecimal minOrderAmount;
    private final LocalDateTime issueStartAt;
    private final LocalDateTime issueEndAt;
    private final Integer validityDays;
    private final String status;            // ACTIVE, PAUSED, EXPIRED, DELETED

    // Infrastructure DTO → Application DTO 변환
    public static CouponRes from(CouponDTO couponDTO) {
        return CouponRes.builder()
                .couponId(couponDTO.getCouponId())
                .name(couponDTO.getName())
                .discountType(couponDTO.getDiscountType())
                .discountValue(couponDTO.getDiscountValue())
                .maxDiscountAmount(couponDTO.getMaxDiscountAmount())
                .minOrderAmount(couponDTO.getMinOrderAmount())
                .issueStartAt(couponDTO.getIssueStartAt())
                .issueEndAt(couponDTO.getIssueEndAt())
                .validityDays(couponDTO.getValidityDays())
                .status(couponDTO.getStatus())
                .build();
    }
}