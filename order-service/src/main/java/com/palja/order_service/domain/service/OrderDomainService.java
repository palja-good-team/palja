package com.palja.order_service.domain.service;

import com.palja.order_service.domain.repository.OrderDeliveryRepository;
import com.palja.order_service.domain.repository.OrderItemRepository;
import com.palja.order_service.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDomainService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;

}