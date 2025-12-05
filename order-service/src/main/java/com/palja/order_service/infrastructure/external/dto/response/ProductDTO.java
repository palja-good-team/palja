package com.palja.order_service.infrastructure.external.dto.response;

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
    private String name;
    private String description;
    private String companyName;
    private int stock;
    private BigDecimal price;
    private String category;

    // TODO: 상품 서비스 연동 전까지 사용하는 더미 데이터. product-service 연결 후 삭제.
    public static ProductDTO dummy(UUID productId) {
        return new ProductDTO(
                productId,
                "샘플 상품",
                "샘플 상품 설명입니다.",
                "팔자컴퍼니",
                100,
                BigDecimal.valueOf(10000),
                "FOOD"
        );
    }
}