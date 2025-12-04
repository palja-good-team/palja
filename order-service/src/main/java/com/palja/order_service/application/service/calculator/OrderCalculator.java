package com.palja.order_service.application.service.calculator;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.CouponRes;
import com.palja.order_service.application.dto.ProductRes;
import com.palja.order_service.application.dto.TimeDealRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

// 주문 금액 계산을 담당
@Slf4j
@Component
public class OrderCalculator {

    /**
     * 쿠폰 할인 전 총 금액 계산
     */
    public BigDecimal calculateAmountBeforeCoupon(
            ProductRes product,
            TimeDealRes timeDeal,
            int quantity) {

        BigDecimal unitPrice = (timeDeal != null)
                ? timeDeal.getTimeDealPrice()
                : product.getPrice();

        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * 쿠폰 할인 금액 계산
     */
    public BigDecimal calculateCouponDiscount(CouponRes coupon, BigDecimal orderAmount) {
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        if (coupon.getDiscountType() == null) {
            throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);
        }

        BigDecimal discountAmount;

        if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            // 퍼센트 할인
            discountAmount = calculatePercentageDiscount(coupon.getDiscountValue(), orderAmount);
        } else if ("FIXED".equalsIgnoreCase(coupon.getDiscountType())) {
            // 정액 할인
            discountAmount = BigDecimal.valueOf(coupon.getDiscountValue());
        } else {
            throw new BusinessException(OrderErrorCode.COUPON_NOT_AVAILABLE);
        }

        // 최대 할인 금액 제한
        if (coupon.getMaxDiscountAmount() != null
                && discountAmount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
            discountAmount = coupon.getMaxDiscountAmount();
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

    private BigDecimal calculatePercentageDiscount(int discountValue, BigDecimal orderAmount) {
        BigDecimal rate = BigDecimal.valueOf(discountValue)
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return orderAmount.multiply(rate)
                .setScale(0, RoundingMode.FLOOR); // 원 단위 절사
    }
}