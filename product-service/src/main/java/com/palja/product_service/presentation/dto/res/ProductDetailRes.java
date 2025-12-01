package com.palja.product_service.presentation.dto.res;

import com.palja.product_service.application.dto.CreateProductRes;
import com.palja.product_service.domain.vo.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProductDetailRes {

    private UUID productId;
    private String name;
    private String description;
    private Money price;
    private String category;
    private BigDecimal avgRating;
    private String companyName;

    public ProductDetailRes (CreateProductRes productRes) {

        this.productId = productRes.getProductId();
        this.name = productRes.getName();
        this.description = productRes.getDescription();
        this.price = productRes.getPrice();
        this.category = productRes.getCategory();
        this.avgRating = productRes.getAvgRating();
        this.companyName = productRes.getCompanyName();
    }
}
