package com.palja.timedeal_service.application.dto.common;

import com.palja.timedeal_service.domain.entity.TimeDealStatusHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TimeDealStatusHistoryRes {

    private String previousStatus;
    private String newStatus;
    private String reason;

    public static TimeDealStatusHistoryRes from(TimeDealStatusHistory history) {
        return TimeDealStatusHistoryRes.builder()
                .previousStatus(history.getPreviousStatus().name())
                .newStatus(history.getNewStatus().name())
                .reason(history.getReason())
                .build();
    }
}
