package com.palja.product_service.presentation.dto.res;

import com.palja.product_service.application.dto.CreateProductRes;
import com.palja.product_service.domain.vo.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class ProductCreateRes {

    private UUID productId;
    private String name;
    private String description;
    private Money price;
    private String category;
    private BigDecimal avgRating;
    private String companyName;
    private Integer stock;

    public static ProductCreateRes fromRes(CreateProductRes productRes) {
        ProductCreateRes result = new ProductCreateRes();

        result.productId = productRes.getProductId();
        result.name = productRes.getName();
        result.description = productRes.getDescription();
        result.price = productRes.getPrice();
        result.category = productRes.getCategory();
        result.avgRating = productRes.getAvgRating();
        result.companyName = productRes.getCompanyName();
        result.stock = productRes.getStock();

        return result;
    }
}
