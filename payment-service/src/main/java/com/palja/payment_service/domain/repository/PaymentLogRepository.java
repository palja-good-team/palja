package com.palja.payment_service.domain.repository;

import com.palja.payment_service.domain.entity.PaymentLog;

public interface PaymentLogRepository {
    PaymentLog save(PaymentLog paymentLog);
}
