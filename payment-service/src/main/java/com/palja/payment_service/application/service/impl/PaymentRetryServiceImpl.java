package com.palja.payment_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.PaymentRetryAction;
import com.palja.payment_service.application.service.PaymentRetryService;
import com.palja.payment_service.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentRetryServiceImpl implements PaymentRetryService {

    private static final Duration TTL = Duration.ofMinutes(5);
    private static final int MAX_FAILURES = 3;

    private final StringRedisTemplate redis;

    @Override
    public void assertNotBlocked(UUID paymentId, PaymentRetryAction action) {
        int count = getFailureCount(paymentId, action);
        if (count >= MAX_FAILURES) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_RETRY_BLOCKED);
        }
    }

    @Override
    public void recordFailure(UUID paymentId, PaymentRetryAction action) {
        String key = key(paymentId, action);

        Long newCount = redis.opsForValue().increment(key);

        if (newCount != null && newCount == 1L) {
            redis.expire(key, TTL);
        }

        if (newCount != null && newCount >= MAX_FAILURES) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_RETRY_BLOCKED);
        }
    }

    @Override
    public void clear(UUID paymentId, PaymentRetryAction action) {
        redis.delete(key(paymentId, action));
    }

    @Override
    public int getFailureCount(UUID paymentId, PaymentRetryAction action) {
        String v = redis.opsForValue().get(key(paymentId, action));
        if (v == null) return 0;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            redis.delete(key(paymentId, action));
            return 0;
        }
    }

    @Override
    public Duration ttl() {
        return TTL;
    }

    @Override
    public int maxFailures() {
        return MAX_FAILURES;
    }

    private String key(UUID paymentId, PaymentRetryAction action) {
        return "pay:retry:" + action.name() + ":" + paymentId;
    }
}
