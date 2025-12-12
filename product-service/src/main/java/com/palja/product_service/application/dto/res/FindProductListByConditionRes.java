package com.palja.product_service.application.dto.res;

import com.palja.product_service.domain.dto.res.FindProductListByConditionDto;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class FindProductListByConditionRes {

    private UUID productId;
    private String name;
    private String description;
    private String price;
    private String category;
    private BigDecimal avgRating;

    public static FindProductListByConditionRes fromDto(FindProductListByConditionDto dto) {
        FindProductListByConditionRes result = new FindProductListByConditionRes();

        result.productId = dto.getProductId();
        result.name = dto.getName();
        result.description = dto.getDescription().length() > 50 ?
                dto.getDescription().substring(0, 50)+ "..." : dto.getDescription();
        result.price = dto.getPrice().toString();
        result.category = dto.getCategory().name();
        result.avgRating = dto.getAvgRating();

        return result;
    }
}
