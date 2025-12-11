package com.palja.product_service.domain.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProductInfoForTimeDealDto {

    private UUID productId;
    private UUID companyUserId;
    private Long price;
    private Long stock;
}
