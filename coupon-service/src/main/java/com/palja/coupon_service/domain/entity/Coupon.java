package com.palja.coupon_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.coupon_service.domain.vo.CouponStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import com.palja.coupon_service.exception.CouponErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType; // 할인 타입

    @Column(nullable = false)
    private Integer discountValue; // 할인율(%) 또는 할인금액

    private Integer totalQuantity; // 총 발급 가능 수량

    @Builder.Default
    private Integer issuedQuantity = 0; // 현재 발급된 수량

    private Integer maxDiscountAmount; // 최대 할인 금액

    private Integer minOrderAmount; // 최소 주문 금액

    private LocalDateTime issueStartAt; // 발급 시작일

    private LocalDateTime issueEndAt; // 발급 종료일

    @Column(nullable = false)
    private Integer validityDays; // 유효 기간

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    public static Coupon create(
            String name,
            String description,
            DiscountType discountType,
            Integer discountValue,
            Integer totalQuantity,
            Integer maxDiscountAmount,
            Integer minOrderAmount,
            LocalDateTime issueStartAt,
            LocalDateTime issueEndAt,
            Integer validityDays
    ) {
        validateDiscount(discountType, discountValue);
        validateIssueDate(issueStartAt, issueEndAt);

        return Coupon.builder()
                .name(name)
                .description(description)
                .discountType(discountType)
                .discountValue(discountValue)
                .totalQuantity(totalQuantity)
                .maxDiscountAmount(maxDiscountAmount)
                .minOrderAmount(minOrderAmount)
                .issueStartAt(issueStartAt)
                .issueEndAt(issueEndAt)
                .validityDays(validityDays)
                .status(CouponStatus.ACTIVE)
                .build();
    }

    private static void validateDiscount(DiscountType discountType, Integer discountValue) {
        if (discountValue == null || discountValue < 1) {
            throw new IllegalArgumentException(CouponErrorCode.INVALID_DISCOUNT_VALUE.getMessage()); // TODO. BusinessException 적용 필요
        }

        if (discountType.equals(DiscountType.PERCENTAGE)) {
            if (discountValue > 100) {
                throw new IllegalArgumentException(CouponErrorCode.INVALID_DISCOUNT_VALUE.getMessage()); // TODO. BusinessException 적용 필요
            }
        }
    }

    private static void validateIssueDate(LocalDateTime issueStartAt, LocalDateTime issueEndAt) {
        if (issueStartAt != null && issueEndAt != null) {
            if (issueStartAt.isAfter(issueEndAt)) {
                throw new IllegalArgumentException(CouponErrorCode.INVALID_DATE_RANGE.getMessage()); // TODO. BusinessException 적용 필요
            }
        }
    }
}
