package com.palja.payment_service.application.service;

import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;

public interface PaymentService {

    PaymentDetailRes createPayment(CreatePaymentCommand command);
}
