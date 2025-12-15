package com.palja.timedeal_service.application.dto.common;

import com.palja.timedeal_service.domain.vo.Period;
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

    public static PeriodRes from(Period period) {
        return PeriodRes.builder()
                .startAt(period.getStartAt())
                .endAt(period.getEndAt())
                .build();
    }
}
