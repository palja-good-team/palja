package com.palja.order_service.application.service;

import com.palja.order_service.application.command.RegisterDeliveryCommand;
import com.palja.order_service.application.command.UpdateDeliveryStatusCommand;
import com.palja.order_service.application.dto.response.DeliveryRegisterRes;
import com.palja.order_service.application.dto.response.DeliveryStatusRes;

public interface OrderDeliveryService {

    DeliveryRegisterRes registerDeliveryTracking(RegisterDeliveryCommand command);

    // 관리자용
    DeliveryStatusRes updateDeliveryStatus(UpdateDeliveryStatusCommand command);
}