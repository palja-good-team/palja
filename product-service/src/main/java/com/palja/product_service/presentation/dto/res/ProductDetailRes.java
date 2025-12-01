package com.palja.product_service.presentation.dto.res;

import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.vo.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailRes {

    private UUID productId;
    private String name;
    private String description;
    private Money price;
    private String category;
    private BigDecimal avgRating;
    private String companyName;

    public static ProductDetailRes fromEntity(Product product) {
        ProductDetailRes result = new ProductDetailRes();

        result.productId = product.getId();
        result.name = product.getName();
        result.description = product.getDescription();
        result.price = product.getPrice();
        result.category = product.getCategory().name();
        result.avgRating = product.getAvgRating();
        result.companyName = product.getCompanyName();

        return result;
    }
}
