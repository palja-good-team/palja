package com.palja.product_service.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FindListByConditionReq {

    private String name;
    private Long minPrice;
    private Long maxPrice;
    private String category;
    private BigDecimal minRating;
    private BigDecimal maxRating;
}
