package com.palja.timedeal_service.domain.vo;

import com.palja.common.exception.BusinessException;
import com.palja.timedeal_service.common.TimeDealErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Period {

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;

    private Period(LocalDateTime startAt, LocalDateTime endAt) {
        validate(startAt, endAt);

        this.startAt = startAt;
        this.endAt = endAt;
    }

    public static Period of(LocalDateTime startAt, LocalDateTime endAt) {
        return new Period(startAt, endAt);
    }

    private static void validate(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null) {
            throw new BusinessException(TimeDealErrorCode.PERIOD_REQUIRED);
        }

        if(!startAt.isAfter(LocalDateTime.now())) {
            throw new BusinessException(TimeDealErrorCode.PERIOD_START_TIME_INVALID);
        }

        if (endAt.isBefore(startAt)) {
            throw new BusinessException(TimeDealErrorCode.PERIOD_END_BEFORE_START);
        }
    }
}
