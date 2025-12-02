package com.palja.order_service.application.service.impl;

import com.palja.order_service.application.service.OrderItemService;
import com.palja.order_service.domain.repository.OrderRepository;
import com.palja.order_service.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
}