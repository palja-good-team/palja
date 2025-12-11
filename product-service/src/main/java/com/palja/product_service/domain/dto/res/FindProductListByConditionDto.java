package com.palja.product_service.domain.dto.res;

import com.palja.product_service.domain.vo.Category;
import com.palja.product_service.domain.vo.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class FindProductListByConditionDto {

    private UUID productId;
    private String name;
    private String description;
    private Money price;
    private Category category;
    private BigDecimal avgRating;
}
