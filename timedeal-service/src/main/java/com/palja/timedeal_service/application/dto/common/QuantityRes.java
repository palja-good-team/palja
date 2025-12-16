package com.palja.timedeal_service.application.dto.common;

import com.palja.timedeal_service.domain.vo.Quantity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class QuantityRes {
    private long totalQuantity;
    private long remainingQuantity;

    public static QuantityRes from(Quantity quantity) {
        return QuantityRes.builder()
                .totalQuantity(quantity.getTotalQuantity())
                .remainingQuantity(quantity.getRemainingQuantity())
                .build();
    }
}
