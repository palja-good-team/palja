package com.palja.product_service.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FindProductListByConditionCommand {

    private String name;
    private Long minPrice;
    private Long maxPrice;
    private String category;
    private BigDecimal minRating;
    private BigDecimal maxRating;
}
