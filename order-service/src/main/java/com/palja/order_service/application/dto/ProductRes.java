package com.palja.order_service.application.dto;

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
            UUID productId,
            String productName,
            BigDecimal price,
            int stockQuantity
    ) {
        // TODO: 상품 도메인 조회 시 companyUserId 반환하도록 수정되면
        //       랜덤 UUID 대신 실제 companyUserId 값을 설정.
        return ProductRes.builder()
                .companyUserId(UUID.randomUUID())
                .productId(productId)
                .productName(productName)
                .price(price)
                .stockQuantity(stockQuantity)
                .build();
    }
}