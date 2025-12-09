package com.palja.order_service.application.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeDealRes {

    private final UUID timeDealId;
    private final UUID productId;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final BigDecimal timeDealPrice;
    private final int discountRate;
    private final int timeDealStockQuantity;
    private final String status;
}
