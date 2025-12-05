package com.palja.payment_service.infrastructure.repository;

import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.vo.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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
    public List<PaymentLog> findByPaymentId(UUID paymentId) {
        return paymentLogJpaRepository.findAllByPayment_IdOrderByProcessedAtDesc(paymentId);
    }

    @Override
    public Page<PaymentLog> findLogs(
            UUID paymentId,
            UUID orderId,
            PaymentStatus status,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PageRequest pageRequest
    ){
        return paymentLogQueryDSLRepository.findLogs(
                paymentId, orderId, status, startDate, endDate, pageRequest
        );
    }

    @Override
    public List<PaymentLog> findLogsOlder(LocalDateTime cutoffDate) {
        return paymentLogJpaRepository.findLogsOlder(cutoffDate);
    }

    @Override
    public void deleteLogsOlder(LocalDateTime cutoffDate) {
        paymentLogJpaRepository.deleteLogsOlder(cutoffDate);
    }
}
