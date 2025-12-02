package com.palja.order_service.infrastructure.repository.impl;

import com.palja.order_service.domain.repository.OrderItemRepository;
import com.palja.order_service.infrastructure.repository.JpaOrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final JpaOrderItemRepository orderItemRepository;
}