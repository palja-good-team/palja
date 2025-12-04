package com.palja.product_service.application.dto.res;

import com.palja.product_service.domain.entity.Product;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateProductRes {

    private UUID productId;
    private String name;
    private String description;
    private String price;
    private String category;

    public static UpdateProductRes fromEntity(Product product) {
        UpdateProductRes res = new UpdateProductRes();

        res.productId =  product.getId();
        res.name = product.getName();
        res.description = product.getDescription();
        res.price = product.getPrice().toString();
        res.category = product.getCategory().toString();

        return res;
    }
}
