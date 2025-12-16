package com.palja.timedeal_service.application.dto.common;

import com.palja.timedeal_service.domain.vo.Amount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AmountRes {
    private long originalPrice;
    private long timeDealPrice;
    private int discountRate;

    public static AmountRes from(Amount amount) {
        return AmountRes.builder()
                .originalPrice(amount.getOriginalPrice())
                .timeDealPrice(amount.getTimeDealPrice())
                .discountRate(amount.getDiscountRate())
                .build();
    }
}
