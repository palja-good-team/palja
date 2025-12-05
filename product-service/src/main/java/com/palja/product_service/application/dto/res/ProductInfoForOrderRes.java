package com.palja.product_service.application.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductInfoForOrderRes {

    private UUID productId;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;
}
