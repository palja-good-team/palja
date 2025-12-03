package com.palja.payment_service.application.service;

import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface PaymentService {

    PaymentDetailRes createPayment(CreatePaymentCommand command);

    PaymentDetailRes cancelPayment(CancelPaymentCommand command);

    PaymentDetailRes getPayment(UUID paymentId);

    Page<PaymentDetailRes> getPayments(PageRequest pageRequest);

    Page<PaymentDetailRes> searchPayments(FindPaymentListByConditionCommand command, PageRequest pageRequest);
}
