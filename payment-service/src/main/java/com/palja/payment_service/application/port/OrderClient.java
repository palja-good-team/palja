package com.palja.payment_service.application.port;

import com.palja.payment_service.application.dto.external.OrderRes;

import java.util.UUID;

public interface OrderClient {
    OrderRes getOrderByOrderId(UUID orderId);
}
