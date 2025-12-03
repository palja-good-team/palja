package com.palja.payment_service.infrastructure.repository;

import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;

    @Override
    public Payment save(Payment payment) {
        return paymentJpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id){
        return paymentJpaRepository.findById(id);
    }

    @Override
    public Page<Payment> findAll(PageRequest pageRequest) {
        return paymentJpaRepository.findAll(pageRequest);
    }
}
