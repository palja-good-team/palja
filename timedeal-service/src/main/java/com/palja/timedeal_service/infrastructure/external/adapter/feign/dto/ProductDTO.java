package com.palja.timedeal_service.infrastructure.external.adapter.feign.dto;

import com.palja.timedeal_service.application.dto.external.ProductInfo;

import java.util.UUID;

public record ProductDTO(
        UUID productId,
        UUID companyUserId,
        long price,
        long stock
) {
    public ProductInfo toInfo() {
        return new ProductInfo(productId, companyUserId, price, stock);
    }
}