package com.palja.coupon_service.domain.vo;

import com.palja.coupon_service.exception.CouponErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiscountPolicy {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private int discountValue;

    public DiscountPolicy(DiscountType discountType, int discountValue) {
        validate(discountType, discountValue);

        this.discountType = discountType;
        this.discountValue = discountValue;
    }

    public static DiscountPolicy of(DiscountType discountType, int discountValue) {
        return new DiscountPolicy(discountType, discountValue);
    }

    private void validate(DiscountType discountType, int discountValue) {
        // TODO. BusinessException 적용 필요
        if (discountType == null)
            throw new IllegalArgumentException(CouponErrorCode.INVALID_DISCOUNT_TYPE.getMessage());

        if (discountValue < 1)
            throw new IllegalArgumentException(CouponErrorCode.INVALID_DISCOUNT_VALUE.getMessage());

        if (discountType.equals(DiscountType.PERCENTAGE)) {
            if (discountValue > 100) {
                throw new IllegalArgumentException(CouponErrorCode.INVALID_DISCOUNT_VALUE.getMessage());
            }
        }
    }
}
