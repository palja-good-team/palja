package com.palja.order_service.application.dto;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.exception.OrderErrorCode;
import com.palja.order_service.infrastructure.external.dto.response.CouponDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        if (couponDTO == null) {
            throw new BusinessException(OrderErrorCode.COUPON_NOT_FOUND);
        }

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

    // 쿠폰 전체 검증
    public void validate(BigDecimal orderAmount) {
        validateStatus();
        validateIssuePeriod();
        validateMinOrderAmount(orderAmount);
    }

    // 쿠폰 상태 검증
    public void validateStatus() {
        if (!"ACTIVE".equals(status)) {
            throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);

        }
    }

    // 기간 검증
    public void validateIssuePeriod() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(issueStartAt)) {
            throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);
        }

        if (now.isAfter(issueEndAt)) {
            throw new BusinessException(OrderErrorCode.COUPON_EXPIRED);
        }
    }

    // 최소 주문 금액 검증
    public void validateMinOrderAmount(BigDecimal orderAmount) {
        if (minOrderAmount == null) return;

        if (orderAmount == null || orderAmount.compareTo(minOrderAmount) < 0) {
            throw new BusinessException(OrderErrorCode.COUPON_MIN_AMOUNT_NOT_MET);
        }
    }

    // 할인 금액 계산
    public BigDecimal calculateDiscountAmount(BigDecimal orderAmount) {
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (discountType == null) {
            throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);
        }

        BigDecimal discountAmount;

        if ("PERCENTAGE".equalsIgnoreCase(discountType)) {
            // 퍼센트 할인 (예: 20 → 20%)
            BigDecimal rate = BigDecimal.valueOf(discountValue)
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

            discountAmount = orderAmount.multiply(rate)
                    .setScale(0, RoundingMode.FLOOR); // 원 단위 절사

        } else if ("FIXED".equalsIgnoreCase(discountType)) {
            // 정액 할인 (예: 5,000원)
            discountAmount = BigDecimal.valueOf(discountValue);

        } else {
            // 그 외 타입은 아직 지원 안 함
            throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);
        }

        // 최대 할인 금액 제한
        if (maxDiscountAmount != null && discountAmount.compareTo(maxDiscountAmount) > 0) {
            discountAmount = maxDiscountAmount;
        }

        // 할인 금액은 주문 금액을 초과할 수 없음
        if (discountAmount.compareTo(orderAmount) > 0) {
            discountAmount = orderAmount;
        }

        if (discountAmount.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return discountAmount;
    }

    // 사용 가능 여부 확인
    public boolean isUsable() {
        LocalDateTime now = LocalDateTime.now();
        return "ACTIVE".equals(status)
                && now.isAfter(issueStartAt)
                && now.isBefore(issueEndAt);
    }
}