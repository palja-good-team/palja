package com.palja.payment_service.application.service;

import com.palja.payment_service.application.dto.response.OrderRes;

import java.util.UUID;

public interface OrderService {
    OrderRes getOrderByOrderId(UUID orderId);
}
