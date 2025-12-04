package com.palja.order_service.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderAmount {

    @Column(name = "product_total_amount", nullable = false)
    private BigDecimal productTotalAmount;

    @Column(name = "item_discount_amount", nullable = false)
    private BigDecimal itemDiscountAmount;

    @Column(name = "coupon_discount_amount")
    private BigDecimal couponDiscountAmount;

    @Column(name = "delivery_fee", nullable = false)
    private BigDecimal deliveryFee;

    @Column(name = "final_amount", nullable = false)
    private BigDecimal finalAmount;

    private OrderAmount(
            BigDecimal productTotalAmount,
            BigDecimal itemDiscountAmount,
            BigDecimal couponDiscountAmount,
            BigDecimal deliveryFee
    ) {
        this.productTotalAmount = defaultZero(productTotalAmount);
        this.itemDiscountAmount = defaultZero(itemDiscountAmount);
        this.couponDiscountAmount = defaultZero(couponDiscountAmount);
        this.deliveryFee = defaultZero(deliveryFee);
        this.finalAmount = calculateFinalAmount();
    }

    public static OrderAmount of(
            BigDecimal productTotalAmount,
            BigDecimal itemDiscountAmount,
            BigDecimal couponDiscountAmount,
            BigDecimal deliveryFee
    ) {
        return new OrderAmount(productTotalAmount, itemDiscountAmount, couponDiscountAmount, deliveryFee);
    }

    private BigDecimal calculateFinalAmount() {
        BigDecimal result = productTotalAmount
                .subtract(itemDiscountAmount)
                .subtract(couponDiscountAmount)
                .add(deliveryFee);

        // 최종 금액은 최소 0원
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return result;
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 쿠폰이 나중에 적용되는 경우 withCouponDiscount 메서드를 추가해서
     * 금액 변경은 항상 VO를 통하게 만듬.
     */
    public OrderAmount withCouponDiscount(BigDecimal couponDiscountAmount) {
        return new OrderAmount(
                this.productTotalAmount,
                this.itemDiscountAmount,
                couponDiscountAmount,
                this.deliveryFee
        );
    }
}