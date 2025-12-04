package com.palja.payment_service.infrastructure.repository;

import com.palja.payment_service.domain.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentLogJpaRepository extends JpaRepository<PaymentLog, UUID> {
    Optional<PaymentLog> findByPayment_InOrderByProcessedAtDesc(UUID paymentId);
}
