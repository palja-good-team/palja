package com.palja.product_service.application.dto.res;

import com.palja.product_service.domain.entity.Product;
import com.palja.product_service.domain.vo.Money;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreateProductRes {

    private UUID productId;
    private String name;
    private String description;
    private String price;
    private String category;
    private BigDecimal avgRating;
    private String companyName;
    private Integer stock;

    public static CreateProductRes fromEntity(Product product) {
        CreateProductRes result = new CreateProductRes();

        result.productId = product.getId();
        result.name = product.getName();
        result.description = product.getDescription();
        result.price = product.getPrice().toString();
        result.category = product.getCategory().name();
        result.avgRating = product.getAvgRating();
        result.companyName = product.getCompanyName();
        result.stock = product.getProductStock().getQuantity();

        return result;
    }
}
