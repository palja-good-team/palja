package com.palja.order_service.domain.repository;

import com.palja.order_service.domain.entity.Order;

public interface OrderRepository {

    Order save(Order order);
}