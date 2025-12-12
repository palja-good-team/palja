package com.palja.payment_service.application.service.impl;

import com.palja.common.auditor.CurrentUser;
import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.common.vo.UserRole;
import com.palja.payment_service.application.command.CancelPaymentCommand;
import com.palja.payment_service.application.command.CompletePaymentCommand;
import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.application.command.FindPaymentListByConditionCommand;
import com.palja.payment_service.application.dto.external.OrderRes;
import com.palja.payment_service.application.dto.external.UserRes;
import com.palja.payment_service.application.dto.response.*;
import com.palja.payment_service.application.dto.response.ReadPaymentDetailRes;
import com.palja.payment_service.application.port.OrderClient;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.application.port.UserClient;
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
    private final OrderClient orderClient;
    private final UserClient userClient;

    @Override
    @Transactional
    /*
      결제 생성 (PENDING 상태)
      - 결제 엔티티만 생성하고 PENDING 상태로 저장
      - Toss API 호출은 하지 않음
     */
    public CreatePaymentRes createPayment(CreatePaymentCommand command) {
        log.info("결제 생성 시작: orderId={}, userId={}, loginId={}, orderStatus={}",
                command.orderId(), command.userId(), command.loginId(), command.orderStatus());

//        OrderRes order = orderClient.getOrderByOrderId(command.orderId());
//
//        if (command.userId() != null && !command.userId().equals(order.getUserId())) {
//            log.error("주문의 userId와 요청 userId가 일치하지 않음: orderUserId={}, requestUserId={}",
//                    order.getUserId(), command.userId());
//            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
//        }

        UserRes user = userClient.getUserByLoginId(command.loginId());

//        paymentValidator.validateCreatePayment(command, order, user);

        Payment payment = Payment.createPending(
                command.orderId(),
                user.getUserId(),
                command.amount()
        );

        paymentRepository.save(payment);

        PaymentLog requestLog = createRequestLog(payment);
        paymentLogRepository.save(requestLog);

        log.info("결제 생성 완료 (PENDING 상태): paymentId={}, orderId={}, userId={}",
                payment.getId(), payment.getOrderId(), payment.getUserId());
        return CreatePaymentRes.from(payment);
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    /*
      결제 완료 처리
      - paymentKey를 받아서 Toss API 호출
      - 결제 확인 후 APPROVED 상태로 변경
      - TODO: 주문 상태를 PAID로 변경하는 API 호출 필요
     */
    public CreatePaymentRes completePayment(CompletePaymentCommand command) {
        log.info("결제 완료 처리 시작: paymentId={}, paymentKey={}, loginId={}",
                command.paymentId(), command.paymentKey(), command.loginId());

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.error("PENDING 상태가 아닌 결제는 완료 처리할 수 없습니다. paymentId={}, status={}",
                    payment.getId(), payment.getStatus());
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }

        // paymentKey 업데이트하고 Toss API 호출해서 결제 확인
        payment.updatePaymentKey(command.paymentKey());

        PGPaymentRes pgRes;
        try {
            pgRes = pgPaymentService.requestPayment(payment);
        } catch (Exception e) {
            log.error("Toss 결제 확인 API 호출 실패: paymentId={}, paymentKey={}",
                    payment.getId(), command.paymentKey(), e);
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }

        PaymentLog requestLog = createRequestLog(payment);
        paymentLogRepository.save(requestLog);

        if (pgRes.isSuccess()) {
            approvePayment(payment, pgRes.getPaymentKey());
            log.info("결제 완료 성공: paymentId={}, orderId={}", payment.getId(), payment.getOrderId());

            // TODO: order-service API 호출하여 주문 상태를 PAID로 변경
        } else {
            failPayment(payment, pgRes.getPgResponseMessage());
            log.warn("결제 완료 실패: paymentId={}, reason={}", payment.getId(), pgRes.getPgResponseMessage());
        }

        paymentRepository.save(payment);

        PaymentLog resultLog = createResultLog(payment, pgRes);
        paymentLogRepository.save(resultLog);

        if (!pgRes.isSuccess()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_FAILED);
        }

        log.info("결제 생성 완료: paymentId={}, userId={}", payment.getId(), payment.getUserId());
        return CreatePaymentRes.from(payment);
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public CancelPaymentRes cancelPayment(CancelPaymentCommand command) {
        log.info("결제 취소 시작: paymentId={}, loginId={}", command.paymentId(), command.loginId());

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        UserRes user = userClient.getUserByLoginId(command.loginId());

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
        return CancelPaymentRes.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadPaymentDetailRes getPayment(UUID paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        String loginId = CurrentUser.getLoginId();
        UserRes user = userClient.getUserByLoginId(loginId);

        paymentValidator.validateGetPayment(payment, user);

        return ReadPaymentDetailRes.from(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReadPaymentSummaryRes> searchPayments(FindPaymentListByConditionCommand command,
                                                      PageRequest pageRequest) {

        String loginId = CurrentUser.getLoginId();
        UserRes user = userClient.getUserByLoginId(loginId);

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

        return payments.map(ReadPaymentSummaryRes::from);
    }

    @Override
    @Transactional
    public void deletePayment(UUID paymentId){
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        String loginId = CurrentUser.getLoginId();
        UserRes user = userClient.getUserByLoginId(loginId);

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
