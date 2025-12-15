package com.palja.timedeal_service.application.dto.common;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuantityRes {
    private long totalQuantity;
    private long remainingQuantity;

    public static QuantityRes from(TimeDeal timeDeal) {
        return QuantityRes.builder()
                .totalQuantity(timeDeal.getTimeDealStock().getQuantity().getTotalQuantity())
                .remainingQuantity(timeDeal.getTimeDealStock().getQuantity().getRemainingQuantity())
                .build();
    }
}
