package com.palja.product_service.application.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SaleProductRes {

    private UUID productId;
    private Boolean status;
}
