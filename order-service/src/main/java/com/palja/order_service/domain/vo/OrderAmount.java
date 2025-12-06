package com.palja.order_service.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
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

    public static OrderAmount create(
            BigDecimal productTotalAmount,
            BigDecimal itemDiscountAmount,
            BigDecimal couponDiscountAmount,
            BigDecimal deliveryFee
    ) {
        BigDecimal validatedProductTotal   = defaultZero(productTotalAmount);
        BigDecimal validatedItemDiscount   = defaultZero(itemDiscountAmount);
        BigDecimal validatedCouponDiscount = defaultZero(couponDiscountAmount);
        BigDecimal validatedDeliveryFee    = defaultZero(deliveryFee);

        BigDecimal finalAmount = calculateFinalAmount(
                validatedProductTotal,
                validatedItemDiscount,
                validatedCouponDiscount,
                validatedDeliveryFee
        );

        return OrderAmount.builder()
                .productTotalAmount(validatedProductTotal)
                .itemDiscountAmount(validatedItemDiscount)
                .couponDiscountAmount(validatedCouponDiscount)
                .deliveryFee(validatedDeliveryFee)
                .finalAmount(finalAmount)
                .build();
    }

    // ===== 금액 계산 =====
    private static BigDecimal calculateFinalAmount(
            BigDecimal productTotalAmount,
            BigDecimal itemDiscountAmount,
            BigDecimal couponDiscountAmount,
            BigDecimal deliveryFee
    ) {
        BigDecimal result = productTotalAmount
                .subtract(itemDiscountAmount)
                .subtract(couponDiscountAmount)
                .add(deliveryFee);

        return result.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : result;
    }

    private static BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}