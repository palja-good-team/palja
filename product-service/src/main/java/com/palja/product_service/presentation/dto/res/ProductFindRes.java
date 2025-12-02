package com.palja.product_service.presentation.dto.res;

import com.palja.product_service.application.dto.FindProductRes;
import com.palja.product_service.domain.vo.Money;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductFindRes {

    private UUID productId;
    private String name;
    private String description;
    private Money price;
    private String category;
    private BigDecimal avgRating;

    public static ProductFindRes fromRes(FindProductRes res) {
        ProductFindRes result = new ProductFindRes();

        result.productId = res.getProductId();
        result.name = res.getName();
        result.description = res.getDescription();
        result.price = res.getPrice();
        result.category = res.getCategory();
        result.avgRating = res.getAvgRating();

        return result;
    }
}
