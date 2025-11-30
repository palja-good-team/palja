package com.palja.payment_service.application.service;

import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.domain.entity.Payment;

public interface PGPaymentService {

    PGPaymentRes requestPayment(Payment payment);
}
