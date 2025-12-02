package com.palja.product_service.application.dto.res;

import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.vo.Money;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class FindProductListByConditionRes {

    private UUID productId;
    private String name;
    private String description;
    private Money price;
    private String category;
    private BigDecimal avgRating;

    public static FindProductListByConditionRes fromEntity(Product product) {
        FindProductListByConditionRes result = new FindProductListByConditionRes();

        result.productId = product.getId();
        result.name = product.getName();
        result.description = product.getDescription().length() > 50 ?
                product.getDescription().substring(0, 50)+ "..." : product.getDescription();
        result.price = product.getPrice();
        result.category = product.getCategory().name();
        result.avgRating = product.getAvgRating();

        return result;
    }
}
