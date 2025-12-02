package com.palja.payment_service.infrastructure.repository;

import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentLogRepositoryImpl implements PaymentLogRepository {

    private final PaymentLogJpaRepository paymentLogJpaRepository;

    @Override
    public PaymentLog save(PaymentLog paymentLog) {
        return paymentLogJpaRepository.save(paymentLog);
    }
}
