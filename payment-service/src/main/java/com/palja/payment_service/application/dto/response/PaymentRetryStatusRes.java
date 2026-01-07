package com.palja.payment_service.application.dto.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class PaymentRetryStatusRes {

    private int failureCount;
    private int maxFailures;
    private boolean blocked;
    private long remainingTtlSeconds;

    public static PaymentRetryStatusRes of(
            int failureCount,
            int maxFailures,
            boolean blocked,
            Duration remainingTtl
    ) {
        return PaymentRetryStatusRes.builder()
                .failureCount(failureCount)
                .maxFailures(maxFailures)
                .blocked(blocked)
                .remainingTtlSeconds(remainingTtl != null && !remainingTtl.isZero() 
                        ? remainingTtl.getSeconds() 
                        : 0L)
                .build();
    }
}

