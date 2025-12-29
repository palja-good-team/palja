package com.palja.timedeal_service.application.event.dto.request.out;

import com.palja.timedeal_service.application.event.dto.KafkaEvent;
import com.palja.timedeal_service.application.event.dto.TimeDealEvent;
import lombok.*;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ProductStockDecreaseEventReq implements TimeDealEvent {
    private UUID timeDealId;
    private UUID productId;
    private long quantity;

    public static ProductStockDecreaseEventReq of(UUID timeDealId, UUID productId, long quantity) {
        return ProductStockDecreaseEventReq.builder()
                .timeDealId(timeDealId)
                .productId(productId)
                .quantity(quantity)
                .build();
    }
}