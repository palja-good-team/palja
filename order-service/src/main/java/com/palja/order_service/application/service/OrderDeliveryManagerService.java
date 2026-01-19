package com.palja.order_service.application.service;

import com.palja.order_service.application.command.UpdateDeliveryStatusCommand;
import com.palja.order_service.application.dto.response.DeliveryStatusRes;

public interface OrderDeliveryManagerService {

    DeliveryStatusRes updateDeliveryStatus(UpdateDeliveryStatusCommand command);
}