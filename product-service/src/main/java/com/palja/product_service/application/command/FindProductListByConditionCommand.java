package com.palja.product_service.application.command;

import java.math.BigDecimal;

public record FindProductListByConditionCommand(
        String name,
        Long minPrice,
        Long maxPrice,
        String category,
        BigDecimal minRating,
        BigDecimal maxRating
) {
}
