package com.palja.payment_service.infrastructure.repository;

import com.palja.payment_service.domain.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PaymentLogJpaRepository extends JpaRepository<PaymentLog, UUID> {
    List<PaymentLog> findAllByPayment_IdOrderByProcessedAtDesc(UUID paymentId);

    @Query("SELECT pl FROM PaymentLog pl WHERE pl.processedAt < :cutoffDate")
    List<PaymentLog> findLogsOlder(@Param("cutoffDate") LocalDateTime cutoffDate);

    @Modifying
    @Query("DELETE FROM PaymentLog pl WHERE pl.processedAt < :cutoffDate")
    void deleteLogsOlder(@Param("cutoffDate") LocalDateTime cutoffDate);
}
