package com.palja.order_service.domain.repository;

import com.palja.order_service.domain.entity.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findOrderByIdWithItemAndDelivery(UUID orderId);
}