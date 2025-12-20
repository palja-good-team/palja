package com.palja.order_service.presentation.controller.impl;

import com.palja.order_service.application.service.OrderDeliveryService;
import com.palja.order_service.presentation.controller.OrderDeliveryController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderDeliveryControllerImpl implements OrderDeliveryController {

    private final OrderDeliveryService orderDeliveryService;

}