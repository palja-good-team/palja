package com.palja.product_service.application.command;

import com.palja.product_service.domain.dto.req.FindListByConditionReq;
import com.palja.product_service.domain.vo.Category;
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

    public FindListByConditionReq toDomainCondition(Category category)   {
        return new FindListByConditionReq(
                name,
                minPrice,
                maxPrice,
                category,
                minRating,
                maxRating);
    }
}
