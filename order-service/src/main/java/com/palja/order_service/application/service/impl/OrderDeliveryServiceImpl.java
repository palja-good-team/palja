package com.palja.order_service.application.service.impl;

import com.palja.order_service.application.service.OrderDeliveryService;
import com.palja.order_service.domain.repository.OrderRepository;
import com.palja.order_service.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDeliveryServiceImpl implements OrderDeliveryService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
}