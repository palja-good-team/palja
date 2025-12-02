package com.palja.order_service.infrastructure.repository.impl;

import com.palja.order_service.domain.repository.OrderDeliveryRepository;
import com.palja.order_service.infrastructure.repository.JpaOrderDeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderDeliveryRepositoryImpl implements OrderDeliveryRepository {

    private final JpaOrderDeliveryRepository orderDeliveryRepository;
}