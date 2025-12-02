package com.palja.order_service.presentation.controller;

import com.palja.order_service.application.service.OrderItemDeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderItemDeliveryController {

    private final OrderItemDeliveryService orderItemDeliveryService;

}