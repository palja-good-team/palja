package com.palja.payment_service.infrastructure.repository;

import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.repository.PaymentRepository;
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
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentQueryDSLRepositoryImpl paymentQueryDSLRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id){
        return paymentJpaRepository.findById(id);
    }

    @Override
    public Page<Payment> findPayments(
            PaymentStatus status,
            Long userId,
            UUID orderId,
            LocalDateTime startDate,
            LocalDateTime endDate,
            PageRequest pageRequest
    ){
        return paymentQueryDSLRepository.findPayments(status, userId, orderId, startDate, endDate, pageRequest);
    }

    @Override
    public void deleteById(UUID id) {
        paymentJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentJpaRepository.findByOrderId(orderId);
    }
}
