package com.palja.coupon_service.domain.vo;

import com.palja.coupon_service.exception.CouponErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IssuePeriod {

    @Column
    private LocalDateTime issueStartAt;

    @Column
    private LocalDateTime issueEndAt;

    public IssuePeriod(LocalDateTime startAt, LocalDateTime endAt) {
        validate(startAt, endAt);

        this.issueStartAt = startAt;
        this.issueEndAt = endAt;
    }

    public static IssuePeriod of(LocalDateTime issueStartAt, LocalDateTime endAt) {
        return new IssuePeriod(issueStartAt, endAt);
    }

    private void validate(LocalDateTime issueStartAt, LocalDateTime issueEndAt) {
        if (issueStartAt == null || issueEndAt == null) return;

        if (issueStartAt.isAfter(issueEndAt)) {
            throw new IllegalArgumentException(CouponErrorCode.INVALID_DATE_RANGE.getMessage());
        }
    }
}
