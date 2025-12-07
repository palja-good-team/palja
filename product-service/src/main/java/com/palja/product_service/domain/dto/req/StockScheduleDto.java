package com.palja.product_service.domain.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class StockScheduleDto {

    private UUID productId;
    private int quantity;
}
