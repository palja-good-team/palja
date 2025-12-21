package com.palja.product_service.application.dto.res;

import com.palja.product_service.domain.dto.res.ProductInfoForOrderDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductInfoForOrderRes {

    private UUID productId;
    private UUID companyUserId;
    private String productName;
    private Long price;
    private Long stockQuantity;

    public static ProductInfoForOrderRes fromDto(ProductInfoForOrderDto dto) {

        return new ProductInfoForOrderRes(
                dto.getProductId(),
                dto.getCompanyUserId(),
                dto.getProductName(),
                dto.getPrice(),
                dto.getStockQuantity()
        );
    }
}
