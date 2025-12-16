package com.palja.order_service.domain.repository;

import com.palja.order_service.application.saga.model.OrderSaga;

import java.util.Optional;
import java.util.UUID;

public interface OrderSagaRepository {

    OrderSaga save(OrderSaga orderSaga);

    OrderSaga saveAndFlush(OrderSaga orderSaga);

    // 주문 ID로 Saga 조회
    Optional<OrderSaga> findByOrderId(UUID orderId);
}