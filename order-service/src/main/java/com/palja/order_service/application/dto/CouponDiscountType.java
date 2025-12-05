package com.palja.order_service.application.dto;

public enum CouponDiscountType {
    PERCENTAGE,
    FIXED;

    public static CouponDiscountType from(String value) {
        if (value == null) return null;
        try {
            return CouponDiscountType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}