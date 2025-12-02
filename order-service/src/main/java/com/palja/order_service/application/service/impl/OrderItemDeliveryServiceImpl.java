package com.palja.order_service.application.service.impl;

import com.palja.order_service.application.service.OrderItemDeliveryService;
import com.palja.order_service.domain.repository.OrderItemDeliveryRepository;
import com.palja.order_service.domain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemDeliveryServiceImpl implements OrderItemDeliveryService {

    private final OrderItemDeliveryRepository orderItemDeliveryRepository;
    private final OrderDomainService orderDomainService;
}