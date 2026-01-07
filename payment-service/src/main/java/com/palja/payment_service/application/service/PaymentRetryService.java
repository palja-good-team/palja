package com.palja.payment_service.application.service;

import com.palja.payment_service.application.dto.response.PaymentRetryStatusRes;
import com.palja.payment_service.application.type.PaymentRetryAction;

import java.time.Duration;
import java.util.UUID;

public interface PaymentRetryService {

    void assertNotBlocked(UUID paymentId, PaymentRetryAction action);

    void recordFailure(UUID paymentId, PaymentRetryAction action);

    void clear(UUID paymentId, PaymentRetryAction action);

    int getFailureCount(UUID paymentId, PaymentRetryAction action);

    Duration ttl();

    int maxFailures();

    PaymentRetryStatusRes getRetryStatus(UUID paymentId, PaymentRetryAction action);
}
