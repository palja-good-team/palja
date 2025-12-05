package com.palja.order_service.application.dto;

import com.palja.order_service.domain.entity.Order;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

// 가격 정보
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PricingRes {
    private BigDecimal productTotalAmount;
    private BigDecimal itemDiscountAmount;
    private BigDecimal couponDiscountAmount;
    private BigDecimal deliveryFee;
    private BigDecimal finalAmount;

    public static PricingRes from(Order order) {
        return PricingRes.builder()
                .productTotalAmount(order.getOrderAmount().getProductTotalAmount())
                .itemDiscountAmount(order.getOrderAmount().getItemDiscountAmount())
                .couponDiscountAmount(order.getOrderAmount().getCouponDiscountAmount())
                .deliveryFee(order.getOrderAmount().getDeliveryFee())
                .finalAmount(order.getOrderAmount().getFinalAmount())
                .build();
    }
}