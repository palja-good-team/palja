package com.palja.order_service.application.port;

import com.palja.order_service.application.dto.external.PaymentCancelRes;
import com.palja.order_service.application.dto.external.PaymentCreateRes;
import com.palja.order_service.domain.vo.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentClient {

    PaymentCreateRes createPayment(UUID orderId, Long userId, BigDecimal amount, OrderStatus orderStatus);

    PaymentCancelRes cancelPayment(UUID orderId, UUID paymentId, BigDecimal cancelAmount, String cancelReason);
}