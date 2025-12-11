package com.palja.order_service.infrastructure.external.dto.response;

import com.palja.order_service.application.dto.external.ProductRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private UUID productId;
    private UUID companyUserId;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;

    public  ProductRes toResponse() {
        return ProductRes.builder()
                .companyUserId(companyUserId)
                .productId(productId)
                .productName(productName)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}