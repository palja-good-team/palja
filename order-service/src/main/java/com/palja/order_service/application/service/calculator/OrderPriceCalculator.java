package com.palja.order_service.application.service.calculator;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.dto.external.CouponUserDetailRes;
import com.palja.order_service.application.dto.external.ProductRes;
import com.palja.order_service.application.dto.external.TimeDealRes;
import com.palja.order_service.application.exception.OrderErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

// 주문 금액 계산 담당 컴포넌트
@Slf4j
@Component
public class OrderPriceCalculator {

    private static final BigDecimal PERCENTAGE_BASE = BigDecimal.valueOf(100);
    private static final int PERCENTAGE_SCALE = 4;

    // ===== Product Amount Calculation =====
    // 상품 총액 계산 (쿠폰 적용 전)
    public BigDecimal calculateProductTotal(
            ProductRes product,
            TimeDealRes timeDeal,
            int quantity) {

        BigDecimal unitPrice = resolveUnitPrice(product, timeDeal);
        BigDecimal total = unitPrice.multiply(BigDecimal.valueOf(quantity));

        log.debug("상품 총액 계산 완료 - unitPrice: {}, quantity: {}, total: {}",
                unitPrice, quantity, total);

        return total;
    }

    // 단가 결정 (타임딜 우선)
    private BigDecimal resolveUnitPrice(ProductRes product, TimeDealRes timeDeal) {
        return timeDeal != null
                ? timeDeal.getTimeDealPrice()
                : product.getPrice();
    }

    // ===== Coupon Discount Calculation =====
    // 쿠폰 할인액 계산
    public BigDecimal calculateCouponDiscount(CouponUserDetailRes coupon, BigDecimal orderAmount) {
        if (coupon == null || orderAmount == null) {
            log.warn("쿠폰 할인 계산 입력값 null - coupon: {}, orderAmount: {}",
                    coupon, orderAmount);
            return BigDecimal.ZERO;
        }

        BigDecimal rawDiscount = calculateRawDiscount(coupon, orderAmount);
        BigDecimal finalDiscount = applyDiscountLimits(
                rawDiscount,
                coupon.getMaxDiscountAmount(),
                orderAmount
        );

        log.debug("쿠폰 할인 계산 완료 - couponUserId: {}, type: {}, value: {}, raw: {}, final: {}",
                coupon.getCouponUserId(), coupon.getDiscountType(), coupon.getDiscountValue(),
                rawDiscount, finalDiscount);

        return finalDiscount;
    }

    // 할인 타입별 원시 할인액 계산 (비즈니스 제약이 걸리기 전에 순수 계산 결과)
    private BigDecimal calculateRawDiscount(CouponUserDetailRes coupon, BigDecimal orderAmount) {
        return switch (coupon.getDiscountType()) {
            case FIXED -> calculateFixedDiscount(coupon.getDiscountValue());
            case PERCENTAGE -> calculatePercentageDiscount(coupon.getDiscountValue(), orderAmount);
            default -> throw new BusinessException(OrderErrorCode.INVALID_COUPON_TYPE);
        };
    }

    // 정액 할인 계산
    private BigDecimal calculateFixedDiscount(int discountValue) {
        return BigDecimal.valueOf(discountValue);
    }

    // 정률 할인 계산 (원 단위 절사)
    private BigDecimal calculatePercentageDiscount(int discountPercentage, BigDecimal orderAmount) {
        BigDecimal rate = convertPercentageToRate(discountPercentage);
        BigDecimal discountedAmount = orderAmount.multiply(rate);
        return truncateToWon(discountedAmount);
    }

    // 백분율을 소수 비율로 변환
    private BigDecimal convertPercentageToRate(int percentage) {
        return BigDecimal.valueOf(percentage)
                .divide(PERCENTAGE_BASE, PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }

    // ===== Discount Limits =====
    /**
     * 할인액 상한 적용
     * - 최대 할인액 제한
     * - 주문 금액 초과 방지
     * - 음수 방지
     */
    private BigDecimal applyDiscountLimits(
            BigDecimal discount,
            BigDecimal maxDiscount,
            BigDecimal orderAmount) {

        BigDecimal limited = discount;

        // 1. 최대 할인액 제한
        if (maxDiscount != null && limited.compareTo(maxDiscount) > 0) {
            log.debug("할인액 최대 금액 제한 적용 - original: {}, max: {}",
                    limited, maxDiscount);
            limited = maxDiscount;
        }

        // 2. 주문 금액 초과 방지
        if (limited.compareTo(orderAmount) > 0) {
            log.debug("할인액 주문 금액 제한 적용 - original: {}, orderAmount: {}",
                    limited, orderAmount);
            limited = orderAmount;
        }

        // 3. 음수 방지 (방어)
        if (limited.compareTo(BigDecimal.ZERO) < 0) {
            log.warn("음수 할인액 감지, 0으로 초기화 - discount: {}", limited);
            limited = BigDecimal.ZERO;
        }

        return limited;
    }

    // ===== Utility =====
    // 원 단위 절사 (소수점 버림)
    private BigDecimal truncateToWon(BigDecimal amount) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        return amount.setScale(0, RoundingMode.FLOOR);
    }
}