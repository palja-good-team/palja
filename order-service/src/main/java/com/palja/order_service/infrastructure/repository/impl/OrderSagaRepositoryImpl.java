package com.palja.order_service.infrastructure.repository.impl;

import com.palja.order_service.application.saga.model.OrderSaga;
import com.palja.order_service.domain.repository.OrderSagaRepository;
import com.palja.order_service.infrastructure.repository.JpaOrderSagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderSagaRepositoryImpl implements OrderSagaRepository {

    private final JpaOrderSagaRepository jpaOrderSagaRepository;

    @Override
    @Transactional
    public OrderSaga save(OrderSaga orderSaga) {
        return jpaOrderSagaRepository.save(orderSaga);
    }

    @Override
    @Transactional
    public OrderSaga saveAndFlush(OrderSaga orderSaga) {
        return jpaOrderSagaRepository.saveAndFlush(orderSaga);
    }

    @Override
    public Optional<OrderSaga> findByOrderId(UUID orderId) {
        return jpaOrderSagaRepository.findByOrderId(orderId);
    }
}