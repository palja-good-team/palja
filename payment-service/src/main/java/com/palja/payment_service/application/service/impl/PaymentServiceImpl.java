package com.palja.payment_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.repository.PaymentRepository;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import feign.FeignException;
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
        Payment payment = Payment.create(command.orderId(), command.userId(), command.amount(), command.currency(), command.paymentMethod(), command.paymentKey());
        paymentRepository.save(payment);

        PaymentLog requestLog = createRequestLog(payment);
        paymentLogRepository.save(requestLog);

        PGPaymentRes pgRes;
        try {
            pgRes = pgPaymentService.requestPayment(payment);
        } catch (FeignException e) {
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }

        if (pgRes.isSuccess()) {
            approvePayment(payment, pgRes.getPaymentKey());
        } else {
            failPayment(payment, pgRes.getPgResponseMessage());
        }

        paymentRepository.save(payment);

        PaymentLog resultLog = createResultLog(payment, pgRes);
        paymentLogRepository.save(resultLog);

        if (!pgRes.isSuccess()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_FAILED);
        }

        return PaymentDetailRes.from(payment);
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public PaymentDetailRes cancelPayment(CancelPaymentCommand command) {

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.APPROVED) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_APPROVED);
        }

        PaymentLog requestLog = createRequestLog(payment);
        paymentLogRepository.save(requestLog);

        PGPaymentRes pgRes;
        try {
            pgRes = pgPaymentService.cancelPayment(
                    payment,
                    command.cancelAmount(),
                    command.cancelReason()
            );
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }

        if (pgRes.isSuccess()) {
            payment.cancel(command.cancelReason());
        } else {
            payment.fail(pgRes.getPgResponseMessage());
        }

        paymentRepository.save(payment);

        PaymentLog resultLog = createResultLog(payment, pgRes);
        paymentLogRepository.save(resultLog);

        if (!pgRes.isSuccess()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_FAILED);
        }

        return PaymentDetailRes.from(payment);
    }

    private void approvePayment(Payment payment, String paymentKey) {
        payment.approve(paymentKey);
    }

    private void failPayment(Payment payment, String reason) {
        payment.fail(reason);
    }

    private PaymentLog createRequestLog(Payment payment) {
        return PaymentLog.createRequestLog(payment);
    }

    private PaymentLog createResultLog(Payment payment, PGPaymentRes pgRes) {
        return PaymentLog.createResultLog(
                payment,
                pgRes.getPaymentKey(),
                pgRes.getPgResponseCode(),
                pgRes.getPgResponseMessage()
        );
    }
}
