package com.palja.order_service.infrastructure.repository;

import com.palja.order_service.application.saga.model.OrderSaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaOrderSagaRepository extends JpaRepository<OrderSaga, UUID> {

    Optional<OrderSaga> findByOrderId(UUID orderId);

    Optional<OrderSaga> findBySagaId(UUID sagaId);
}