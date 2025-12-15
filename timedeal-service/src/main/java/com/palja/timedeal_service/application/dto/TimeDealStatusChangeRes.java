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
// TODO. 변경 사유도 같이 내려주기
public class TimeDealStatusChangeRes {
    private UUID timeDealId;
    private UUID productId;
    private UUID companyUserId;
    private String title;
    private String description;
    private PeriodRes period;
    private AmountRes amount;
    private QuantityRes quantity;
    private String timeDealStatus;

    public static TimeDealStatusChangeRes from(TimeDeal timeDeal) {
        return TimeDealStatusChangeRes.builder()
                .timeDealId(timeDeal.getTimeDealId())
                .productId(timeDeal.getProductId())
                .companyUserId(timeDeal.getCompanyUserId())
                .title(timeDeal.getTitle())
                .description(timeDeal.getDescription())
                .period(PeriodRes.from(timeDeal))
                .amount(AmountRes.from(timeDeal))
                .quantity(QuantityRes.from(timeDeal))
                .timeDealStatus(timeDeal.getTimeDealStatus().name())
                .build();
    }
}
