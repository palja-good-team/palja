package com.palja.order_service.application.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRes {
    private UUID companyUserId;
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;

    public static ProductRes of(
            UUID companyUserId,
            UUID productId,
            String productName,
            BigDecimal price,
            int stockQuantity
    ) {
        return ProductRes.builder()
                .companyUserId(companyUserId)
                .productId(productId)
                .productName(productName)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}