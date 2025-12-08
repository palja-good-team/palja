package com.palja.payment_service.application.service.impl;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.vo.UserRole;
import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.response.OrderRes;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import com.palja.payment_service.application.dto.response.UserRes;
import com.palja.payment_service.application.service.OrderService;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.application.service.UserService;
import com.palja.payment_service.application.validator.PaymentValidator;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.repository.PaymentRepository;
import com.palja.payment_service.domain.vo.PaymentMethod;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final PGPaymentService pgPaymentService;
    private final PaymentValidator paymentValidator;
    private final OrderService orderService;
    private final UserService userService;

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    /*
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
        log.info("결제 생성 시작: orderId={}, loginId={}", command.orderId(), command.loginId());

        OrderRes order = orderService.getOrderByOrderId(command.orderId());
        UserRes user = userService.getUserByLoginId(command.loginId());

        paymentValidator.validateCreatePayment(command, order, user);

        PaymentMethod method;
        try{
            method = PaymentMethod.valueOf(command.paymentMethod());
        }catch (IllegalArgumentException | NullPointerException e){
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_METHOD);
        }

        Payment payment = Payment.create(
                command.orderId(),
                user.getUserId(),
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

        log.info("결제 생성 완료: paymentId={}, userId={}", payment.getId(), payment.getUserId());
        return PaymentDetailRes.from(payment);
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public PaymentDetailRes cancelPayment(CancelPaymentCommand command) {
        log.info("결제 취소 시작: paymentId={}, loginId={}", command.paymentId(), command.loginId());

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        UserRes user = userService.getUserByLoginId(command.loginId());

        paymentValidator.validateCancelPayment(payment, command, user);

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

        log.info("결제 취소 완료: paymentId={}", payment.getId());
        return PaymentDetailRes.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDetailRes getPayment(UUID paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        String loginId = CurrentUser.getLoginId();
        UserRes user = userService.getUserByLoginId(loginId);

        paymentValidator.validateGetPayment(payment, user);

        return PaymentDetailRes.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDetailRes> getPayments(PageRequest pageRequest) {
        Page<Payment> payments = paymentRepository.findAll(pageRequest);
        return payments.map(PaymentDetailRes::from);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentDetailRes> searchPayments(FindPaymentListByConditionCommand command,
                                                 PageRequest pageRequest) {

        String loginId = CurrentUser.getLoginId();
        UserRes user = userService.getUserByLoginId(loginId);

        paymentValidator.validateSearchPayments(command, user);

        PaymentStatus status = null;
        if (command.status() != null) {
            try {
                status = PaymentStatus.valueOf(command.status());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
            }
        }

        Long filteredUserId = command.userId();
        if (UserRole.CUSTOMER.equals(user.getRole())) {
            filteredUserId = user.getUserId();
        }

        Page<Payment> payments = paymentRepository.findPayments(
                status,
                filteredUserId,
                command.orderId(),
                command.startDate(),
                command.endDate(),
                pageRequest
        );

        return payments.map(PaymentDetailRes::from);
    }

    @Override
    @Transactional
    public void deletePayment(UUID paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        String loginId = CurrentUser.getLoginId();
        UserRes user = userService.getUserByLoginId(loginId);

        paymentValidator.validateDeletePayment(payment, user);

        if(payment.getStatus() == PaymentStatus.PENDING) {
            payment.softDelete();
            paymentRepository.save(payment);
        }else {
            throw new BusinessException(PaymentErrorCode.PAYMENT_CANNOT_BE_DELETED);
        }
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
