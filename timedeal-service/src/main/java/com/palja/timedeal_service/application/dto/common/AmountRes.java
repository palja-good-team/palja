package com.palja.timedeal_service.application.dto.common;

import com.palja.timedeal_service.domain.entity.TimeDeal;
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

    public static AmountRes from(TimeDeal timeDeal) {
        return AmountRes.builder()
                .originalPrice(timeDeal.getAmount().getOriginalPrice())
                .timeDealPrice(timeDeal.getAmount().getTimeDealPrice())
                .discountRate(timeDeal.getAmount().getDiscountRate())
                .build();
    }
}
