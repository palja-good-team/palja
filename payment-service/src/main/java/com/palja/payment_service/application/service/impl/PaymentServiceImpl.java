package com.palja.payment_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.repository.PaymentRepository;
import com.palja.payment_service.domain.vo.PaymentMethod;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final PGPaymentService pgPaymentService;

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    /*TODO: order-service에서 orderId로 주문을 조회하고
            주문금액과 결제 금액이 동일한지, userId, status(CREATED) 검증하기
            만약, 다르다면 PG 호출하지 말고 Payment 상태값을 FAILED로 저장하고 로그 남기기
      TODO: PaymentLog에 retryCount, success 추가하고
            5~10분 이내 같은 orderId와 userId로 결제 5번 이상 실패하면
            더 이상 PG 호출이 안되도록 막기, order-service에 이벤트 전달 (상태 CREATED->CANCLED)
      TODO: 결제 성공 & 실패 이벤트 발생
            1. PaymentApprovedEvent(orderId, paymentId, userId, amount, paymentKey 등)
             Kafka에 order-service, coupon-service 등이 이 이벤트를 구독해서
             주문 상태 변경, 쿠폰 사용 처리 등을 비동기로 처리
            2. PaymentFailedEvent(orderId, paymentId, userId, 사유, 에러코드 등)
                Kafka에 order-service가 주문 상태를 그래도 CREATED로 유지, coupon-service 도 미사용으로 유지
     */
    public PaymentDetailRes createPayment(CreatePaymentCommand command) {
        PaymentMethod method;
        try{
            method = PaymentMethod.valueOf(command.paymentMethod());
        }catch (IllegalArgumentException | NullPointerException e){
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
        }

        Payment payment = Payment.create(
                command.orderId(),
                command.userId(),
                command.amount(),
                command.currency(),
                method,
                command.paymentKey()
        );

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

    @Override
    @Transactional(readOnly = true)
    public PaymentDetailRes getPayment(UUID paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        return PaymentDetailRes.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDetailRes> getPayments(PageRequest pageRequest) {
        Page<Payment> payments = paymentRepository.findAll(pageRequest);
        return payments.map(PaymentDetailRes::from);
    }

    @Override
    public Page<PaymentDetailRes> searchPayments(FindPaymentListByConditionCommand command, PageRequest pageRequest) {
        Page<Payment> payments = paymentRepository.findPayments(
                command.status(),
                command.userId(),
                command.orderId(),
                command.startDate(),
                command.endDate(),
                pageRequest
        );

        return payments.map(PaymentDetailRes::from);
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
