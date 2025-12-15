package com.palja.timedeal_service.application.dto;

import com.palja.timedeal_service.application.dto.common.AmountRes;
import com.palja.timedeal_service.application.dto.common.PeriodRes;
import com.palja.timedeal_service.application.dto.common.QuantityRes;
import com.palja.timedeal_service.domain.entity.TimeDeal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class TimeDealCreateRes {
    private UUID timeDealId;
    private UUID productId;
    private UUID companyUserId;
    private String title;
    private String description;
    private PeriodRes period;
    private AmountRes amount;
    private QuantityRes quantity;
    private String timeDealStatus;

    public static TimeDealCreateRes from(TimeDeal timeDeal) {
        return TimeDealCreateRes.builder()
                .timeDealId(timeDeal.getTimeDealId())
                .productId(timeDeal.getProductId())
                .companyUserId(timeDeal.getCompanyUserId())
                .title(timeDeal.getTitle())
                .description(timeDeal.getDescription())
                .period(PeriodRes.from(timeDeal.getPeriod()))
                .amount(AmountRes.from(timeDeal.getAmount()))
                .quantity(QuantityRes.from(timeDeal.getTimeDealStock().getQuantity()))
                .timeDealStatus(timeDeal.getTimeDealStatus().name())
                .build();
    }
}
