package com.palja.order_service.application.dto;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.exception.OrderErrorCode;

public enum CouponUserStatus {
    ISSUED,
    USED,
    EXPIRED;

    public static CouponUserStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(OrderErrorCode.COUPON_INVALID_STATUS);
        }

        try {
            return CouponUserStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(OrderErrorCode.COUPON_INVALID_STATUS);
        }
    }
}