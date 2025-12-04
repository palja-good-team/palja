package com.palja.payment_service.domain.repository;

import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.vo.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);

    Optional<Payment> findById(UUID id);

    Page<Payment> findAll(PageRequest pageRequest);

    Page<Payment> findPayments(PaymentStatus status, Long userId, UUID orderId, LocalDateTime startDate, LocalDateTime endDate, PageRequest pageRequest);

    void deleteById(UUID id);
}
