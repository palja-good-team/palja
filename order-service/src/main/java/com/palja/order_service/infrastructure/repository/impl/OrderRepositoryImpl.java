package com.palja.order_service.infrastructure.repository.impl;

import com.palja.order_service.domain.repository.OrderRepository;
import com.palja.order_service.infrastructure.repository.JpaOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final JpaOrderRepository jpaOrderRepository;
}