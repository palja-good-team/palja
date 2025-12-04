package com.palja.coupon_service.domain.vo;

import com.palja.common.exception.BusinessException;
import com.palja.coupon_service.exception.CouponErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AmountPolicy {

    @Column
    private Integer maxDiscountAmount; // 최대 할인 금액

    @Column
    private Integer minOrderAmount; // 최소 주문 금액

    public AmountPolicy(Integer maxDiscountAmount, Integer minOrderAmount) {
        validate(maxDiscountAmount, minOrderAmount);

        this.maxDiscountAmount = maxDiscountAmount;
        this.minOrderAmount = minOrderAmount;
    }

    public static AmountPolicy of(Integer maxDiscountAmount, Integer minOrderAmount) {
        return new AmountPolicy(maxDiscountAmount, minOrderAmount);
    }

    public AmountPolicy update(Integer newMaxDiscountAmount, Integer newMinOrderAmount) {
        Integer updateMaxDiscountAmount = newMaxDiscountAmount != null ? newMaxDiscountAmount : this.maxDiscountAmount;
        Integer updateMinOrderAmount =  newMinOrderAmount != null ? newMinOrderAmount : this.minOrderAmount;
        return AmountPolicy.of(updateMaxDiscountAmount, updateMinOrderAmount);
    }

    private void validate(Integer maxDiscountAmount, Integer minOrderAmount) {
        if (minOrderAmount == null || maxDiscountAmount == null)
            return;

        if (maxDiscountAmount > minOrderAmount)
            throw new BusinessException(CouponErrorCode.INVALID_AMOUNT_RELATIONSHIP);
    }
}
