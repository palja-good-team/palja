package com.palja.payment_service.domain.repository;

import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.vo.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PaymentLogRepository {
    PaymentLog save(PaymentLog paymentLog);

    Optional<PaymentLog> findByPaymentId(UUID paymentId);

    Page<PaymentLog> findLogs(UUID paymnetId, PaymentStatus status, LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest);
}
