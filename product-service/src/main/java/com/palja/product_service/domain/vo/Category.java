package com.palja.product_service.domain.vo;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.product_service.exception.ProductErrorCode;

import java.util.Objects;

public enum Category {
    FOOD, TOOL, CLOTHING;

    public static Category fromString(String value) {
        if(Objects.isNull(value))
            throw new BusinessException(CommonErrorCode.BAD_REQUEST);

        for (Category category : values()) {
            if (category.name().equalsIgnoreCase(value))
                return category;
        }
        throw new BusinessException(ProductErrorCode.NOT_SUPPORT_CATEGORY);
    }
}
