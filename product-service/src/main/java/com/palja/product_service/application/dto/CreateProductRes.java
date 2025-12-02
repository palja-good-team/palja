package com.palja.product_service.application.dto;

import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.vo.Money;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CreateProductRes {

    private UUID productId;
    private String name;
    private String description;
    private Money price;
    private String category;
    private BigDecimal avgRating;
    private String companyName;

    public CreateProductRes(Product product) {
        this.productId = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.category = product.getCategory().name();
        this.avgRating = product.getAvgRating();
        this.companyName = product.getCompanyName();
    }
}
