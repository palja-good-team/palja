package com.palja.payment_service.domain.service;

import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentDomainService {

    public void approvePayment(Payment payment, String paymentKey) {
        payment.approve(paymentKey);
    }

    public void failPayment(Payment payment, String reason) {
        payment.fail(reason);
    }

    public PaymentLog createRequestLog(Payment payment) {
        return PaymentLog.createRequestLog(payment);
    }

    public PaymentLog createResultLog(Payment payment, PGPaymentRes pgRes) {
        return PaymentLog.createResultLog(
                payment,
                pgRes.getPaymentKey(),
                pgRes.getPgResponseCode(),
                pgRes.getPgResponseMessage()
        );
    }
}

