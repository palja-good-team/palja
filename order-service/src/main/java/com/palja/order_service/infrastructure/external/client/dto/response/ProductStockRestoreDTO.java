package com.palja.order_service.infrastructure.external.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockRestoreDTO {
    private UUID productId;
    private boolean status;
}