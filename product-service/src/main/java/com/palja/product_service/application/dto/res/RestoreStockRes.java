package com.palja.product_service.application.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RestoreStockRes {

    private UUID productId;
    private boolean status;
}
