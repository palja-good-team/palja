package com.palja.payment_service.infrastructure.repository;

import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.vo.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentLogRepositoryImpl implements PaymentLogRepository {

    private final PaymentLogJpaRepository paymentLogJpaRepository;
    private final PaymentLogQueryDSLRepositoryImpl paymentLogQueryDSLRepository;

    @Override
    public PaymentLog save(PaymentLog paymentLog) {
        return paymentLogJpaRepository.save(paymentLog);
    }

    @Override
    public Optional<PaymentLog> findByPaymentId(UUID paymentId) {
        return paymentLogJpaRepository.findByPayment_InOrderByProcessedAtDesc(paymentId);
    }

    @Override
    public Page<PaymentLog> findLogs(
            UUID paymentId,
            PaymentStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PageRequest pageRequest
    ){
        return paymentLogQueryDSLRepository.findLogs(
                paymentId, status, startDate, endDate, pageRequest
        );
    }
}
