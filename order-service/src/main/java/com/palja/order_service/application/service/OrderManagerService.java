package com.palja.order_service.application.service;

import com.palja.order_service.application.command.ChangeOrderStatusCommand;
import com.palja.order_service.application.dto.OrderStatusChangeRes;

public interface OrderManagerService {
    OrderStatusChangeRes changeOrderStatus(ChangeOrderStatusCommand command);
}