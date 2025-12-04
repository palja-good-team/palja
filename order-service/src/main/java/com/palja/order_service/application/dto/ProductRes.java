package com.palja.order_service.application.dto;

import com.palja.order_service.infrastructure.external.dto.response.ProductDTO;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
public class ProductRes {
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;

    // Infrastructure DTO → Application DTO 변환
    public static ProductRes from(ProductDTO productDTO) {
        return ProductRes.builder()
                .productId(productDTO.getProductId())
                .productName(productDTO.getName())
                .price(productDTO.getPrice())
                .stockQuantity(productDTO.getStock())
                .build();
    }
}
