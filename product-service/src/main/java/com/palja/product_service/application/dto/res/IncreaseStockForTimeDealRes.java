package com.palja.product_service.application.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class IncreaseStockForTimeDealRes {

    private UUID productId;
    private Boolean status;
}
