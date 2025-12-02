package com.palja.product_service.domain.dto.req;

import com.palja.product_service.domain.vo.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class FindListByConditionReq {

    private String name;
    private Long minPrice;
    private Long maxPrice;
    private Category category;
    private BigDecimal minRating;
    private BigDecimal maxRating;
}
