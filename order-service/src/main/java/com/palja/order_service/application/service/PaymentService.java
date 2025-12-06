package com.palja.order_service.application.service;

import com.palja.order_service.application.dto.PaymentRes;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentRes createPayment(UUID orderId, Long userId, BigDecimal amount, String paymentMethod);
}