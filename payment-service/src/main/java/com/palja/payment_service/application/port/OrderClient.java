package com.palja.payment_service.application.port;

import com.palja.payment_service.application.dto.external.OrderRes;

import java.math.BigDecimal;
import java.util.UUID;

public interface OrderClient {
    OrderRes getOrderByOrderId(UUID orderId);

    void completeOrderPayment(UUID orderId, UUID paymentId, BigDecimal paidAmount);
}
