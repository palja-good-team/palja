package com.palja.product_service.domain.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductInfoForOrderDto {

    private UUID productId;
    private UUID companyUserId;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;
}
