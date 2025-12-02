package com.palja.coupon_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.coupon_service.domain.vo.*;
import com.palja.coupon_service.exception.CouponErrorCode;
import jakarta.persistence.*;
import lombok.*;

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

    @Embedded
    private DiscountPolicy discountPolicy; // 할인율(%) 또는 할인금액

    @Column
    private Integer totalQuantity; // 총 발급 가능 수량

    @Builder.Default
    private Integer issuedQuantity = 0; // 현재 발급된 수량

    @Embedded
    private AmountPolicy amountPolicy;

    @Embedded
    private IssuePeriod issuePeriod; // 발급 시작일, 종료일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatus status;

    public static Coupon create(
            String name,
            String description,
            DiscountPolicy discountPolicy,
            Integer totalQuantity,
            AmountPolicy amountPolicy,
            IssuePeriod issuePeriod
    ) {
        validateRequiredFields(name);
        validateQuantity(totalQuantity);

        return Coupon.builder()
                .name(name)
                .description(description)
                .discountPolicy(discountPolicy)
                .totalQuantity(totalQuantity)
                .amountPolicy(amountPolicy)
                .issuePeriod(issuePeriod)
                .status(CouponStatus.ACTIVE)
                .build();
    }

    // 필수 필드 검증
    private static void validateRequiredFields(String name) {
        // TODO. BusinessException 적용 필요
        if (name == null || name.isBlank())
            throw new IllegalArgumentException(CouponErrorCode.INVALID_COUPON_NAME.getMessage());
    }

    // 수량 정책 검증
    private static void validateQuantity(Integer totalQuantity) {
        if (totalQuantity == null)
            // 무제한 발급
            return;

        if (totalQuantity < 1)
            throw new IllegalArgumentException(CouponErrorCode.INVALID_QUANTITY.getMessage());
    }
}
