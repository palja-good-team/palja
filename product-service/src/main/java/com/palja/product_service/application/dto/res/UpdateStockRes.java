package com.palja.product_service.application.dto.res;

import com.palja.product_service.domain.entity.Product;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UpdateStockRes {

    private UUID productId;
    private Integer stock;

    public static UpdateStockRes fromEntity(Product product) {
        UpdateStockRes res = new UpdateStockRes();

        res.productId = product.getId();
        res.stock = product.getProductStock().getQuantity();

        return res;
    }
}
