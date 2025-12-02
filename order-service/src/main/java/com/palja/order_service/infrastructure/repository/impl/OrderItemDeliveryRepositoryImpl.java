package com.palja.order_service.infrastructure.repository.impl;

import com.palja.order_service.domain.repository.OrderItemDeliveryRepository;
import com.palja.order_service.infrastructure.repository.JpaOrderItemDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderItemDeliveryRepositoryImpl implements OrderItemDeliveryRepository {

    private final JpaOrderItemDeliveryRepository orderItemDeliveryRepository;
}