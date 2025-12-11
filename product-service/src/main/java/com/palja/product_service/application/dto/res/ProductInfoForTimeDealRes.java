package com.palja.product_service.application.dto.res;

import com.palja.product_service.domain.dto.res.ProductInfoForTimeDealDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductInfoForTimeDealRes {

    private UUID productId;
    private UUID companyUserId;
    private Long price;
    private Long stock;

    public static ProductInfoForTimeDealRes fromDto(ProductInfoForTimeDealDto dto) {
        return new ProductInfoForTimeDealRes(
                dto.getProductId(),
                dto.getCompanyUserId(),
                dto.getPrice(),
                dto.getStock()
        );
    }
}
