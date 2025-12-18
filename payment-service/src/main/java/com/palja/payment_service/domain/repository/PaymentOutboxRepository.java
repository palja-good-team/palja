package com.palja.payment_service.domain.repository;

import com.palja.payment_service.domain.entity.PaymentOutbox;
import com.palja.payment_service.domain.event.vo.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutbox, Long> {
    List<PaymentOutbox> findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
