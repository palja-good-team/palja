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

    public Period updateStartAt(LocalDateTime newStartAt) {
        return new Period(newStartAt, this.endAt);
    }

    public Period updateEndAt(LocalDateTime newEndAt) {
        return new Period(this.startAt, newEndAt);
    }

    public boolean isNowWithin(LocalDateTime now) {
        return (now.isEqual(this.startAt) || now.isAfter(this.startAt))
                && (now.isEqual(this.endAt) || now.isBefore(this.endAt));
    }

    public boolean isBeforeStartAt(LocalDateTime now) {
        return now.isBefore(this.startAt);
    }

    private void validate(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt == null || endAt == null) {
            throw new BusinessException(TimeDealErrorCode.PERIOD_REQUIRED);
        }

        if (endAt.isBefore(startAt)) {
            throw new BusinessException(TimeDealErrorCode.PERIOD_END_BEFORE_START);
        }
    }
}
