package com.palja.payment_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final PGPaymentService pgPaymentService;

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public PaymentDetailRes createPayment(CreatePaymentCommand command) {
        Payment payment = command.toEntity();
        paymentRepository.save(payment);

        PaymentLog requestLog = PaymentLog.createRequestLog(payment);
        paymentLogRepository.save(requestLog);

        PGPaymentRes pgRes = pgPaymentService.requestPayment(payment);

        if (pgRes.isSuccess()) {
            payment.approve(pgRes.getPaymentKey());
            paymentRepository.save(payment);

            PaymentLog resultLog = PaymentLog.createResultLog(
                    payment,
                    pgRes.getPaymentKey(),
                    pgRes.getPgResponseCode(),
                    pgRes.getPgResponseMessage()
            );
            paymentLogRepository.save(resultLog);

            return PaymentDetailRes.from(payment);
        } else {
            payment.fail(pgRes.getPgResponseMessage());
            paymentRepository.save(payment);

            PaymentLog resultLog = PaymentLog.createResultLog(
                    payment,
                    pgRes.getPaymentKey(),
                    pgRes.getPgResponseCode(),
                    pgRes.getPgResponseMessage()
            );
            paymentLogRepository.save(resultLog);

            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }
    }
}
