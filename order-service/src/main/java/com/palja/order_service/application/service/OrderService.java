package com.palja.order_service.application.service;

import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.dto.CreateOrderRes;

public interface OrderService {
    CreateOrderRes createOrder(CreateOrderCommand command);
}