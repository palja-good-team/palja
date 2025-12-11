package com.palja.product_service.domain.vo;

import com.palja.common.exception.BusinessException;
import com.palja.product_service.exception.ProductErrorCode;

import java.util.Objects;

public enum Category {
    FOOD, TOOL, CLOTHING, EMPTY;

    public static Category fromString(String value) {
        if(Objects.isNull(value))
            return EMPTY;

        for (Category category : values()) {
            if (category.name().equalsIgnoreCase(value))
                return category;
        }
        throw new BusinessException(ProductErrorCode.NOT_SUPPORT_CATEGORY);
    }
}
