package com.palja.timedeal_service.application.event.dto.request.out;

import com.palja.timedeal_service.application.event.dto.ProductEvent;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProductStockRestoreEventReq implements ProductEvent {
    private UUID timeDealId;
    private UUID productId;
    private long quantity;

    public static ProductStockRestoreEventReq of(UUID timeDealId, UUID productId, long quantity) {
        return ProductStockRestoreEventReq.builder()
                .timeDealId(timeDealId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}
