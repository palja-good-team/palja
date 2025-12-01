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
