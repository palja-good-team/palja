package com.palja.payment_service.application.service;

import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentDetailRes createPayment(CreatePaymentCommand command);

    PaymentDetailRes cancelPayment(CancelPaymentCommand command);

    PaymentDetailRes getPayment(UUID paymentId);

    List<PaymentDetailRes> getPayments(int page, int size);
}
