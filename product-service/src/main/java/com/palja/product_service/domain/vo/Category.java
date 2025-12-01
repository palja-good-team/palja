package com.palja.product_service.domain.vo;

import java.util.Objects;

public enum Category {
    FOOD, TOOL, CLOTHING;

    public static Category fromString(String value) {
        if(Objects.isNull(value))
            throw new IllegalArgumentException();

        for (Category category : values()) {
            if (category.name().equalsIgnoreCase(value))
                return category;
        }
        throw new IllegalArgumentException();
    }
}
