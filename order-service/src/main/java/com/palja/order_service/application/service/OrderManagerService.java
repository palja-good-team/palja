package com.palja.order_service.application.service;

import com.palja.order_service.application.command.OrderStatusChangeCommand;
import com.palja.order_service.application.dto.response.OrderStatusChangeRes;

public interface OrderManagerService {
    OrderStatusChangeRes changeOrderStatus(OrderStatusChangeCommand command);
}