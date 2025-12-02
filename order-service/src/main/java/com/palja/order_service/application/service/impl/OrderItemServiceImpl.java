package com.palja.order_service.application.service.impl;

import com.palja.order_service.application.service.OrderItemService;
import com.palja.order_service.domain.repository.OrderItemRepository;
import com.palja.order_service.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderDomainService orderDomainService;
}