package com.palja.order_service.application.service;

import com.palja.order_service.application.command.RegisterDeliveryCommand;
import com.palja.order_service.application.dto.response.DeliveryRegisterRes;

public interface OrderDeliveryService {

    DeliveryRegisterRes registerDeliveryTracking(RegisterDeliveryCommand command);
}