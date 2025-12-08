package com.palja.payment_service.application.service;

import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.response.CancelPaymentRes;
import com.palja.payment_service.application.dto.response.CreatePaymentRes;
import com.palja.payment_service.application.dto.response.ReadPaymentDetailRes;
import com.palja.payment_service.application.dto.response.ReadPaymentSummaryRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface PaymentService {

    CreatePaymentRes createPayment(CreatePaymentCommand command);

    CancelPaymentRes cancelPayment(CancelPaymentCommand command);

    ReadPaymentDetailRes getPayment(UUID paymentId);

    Page<ReadPaymentSummaryRes> getPayments(PageRequest pageRequest);

    Page<ReadPaymentSummaryRes> searchPayments(FindPaymentListByConditionCommand command, PageRequest pageRequest);

    void deletePayment(UUID paymentId);
}
