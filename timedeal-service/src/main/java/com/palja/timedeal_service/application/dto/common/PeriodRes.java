package com.palja.timedeal_service.application.dto.common;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PeriodRes {
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    public static PeriodRes from(TimeDeal timeDeal) {
        return PeriodRes.builder()
                .startAt(timeDeal.getPeriod().getStartAt())
                .endAt(timeDeal.getPeriod().getEndAt())
                .build();
    }
}
