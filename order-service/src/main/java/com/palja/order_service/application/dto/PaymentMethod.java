package com.palja.order_service.application.dto;

import com.palja.common.exception.BusinessException;
import com.palja.order_service.application.exception.OrderErrorCode;

public enum PaymentMethod {
    CARD,
    ACCOUNT,
    EASY_PAY,
    MOBILE,
    VIRTUAL_ACCOUNT;

    public static PaymentMethod from(String value) {
        if (value == null) {
            throw new BusinessException(OrderErrorCode.INVALID_PAYMENT_METHOD);
        }
        try {
            return PaymentMethod.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException(OrderErrorCode.INVALID_PAYMENT_METHOD);
        }
    }
}