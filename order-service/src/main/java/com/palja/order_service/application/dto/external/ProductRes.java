package com.palja.order_service.application.dto.external;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductRes {
    private UUID companyUserId;
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private int stockQuantity;
}