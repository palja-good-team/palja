package com.palja.order_service.application.service;

import com.palja.order_service.application.dto.PaymentMethod;
import com.palja.order_service.application.dto.response.PaymentCancelRes;
import com.palja.order_service.application.dto.response.PaymentCreateRes;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentCreateRes createPayment(UUID orderId, Long userId, BigDecimal amount, String paymentKey, PaymentMethod paymentMethod);

    PaymentCancelRes cancelPayment(UUID orderId, UUID paymentId, BigDecimal cancelAmount, String cancelReason);
}