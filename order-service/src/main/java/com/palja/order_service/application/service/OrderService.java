package com.palja.order_service.application.service;

import com.palja.order_service.application.command.CreateOrderCommand;
import com.palja.order_service.application.dto.OrderCreateRes;

public interface OrderService {
    OrderCreateRes createOrder(CreateOrderCommand command);
}