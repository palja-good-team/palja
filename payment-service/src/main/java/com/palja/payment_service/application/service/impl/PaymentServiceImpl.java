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

import java.util.Objects;
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
        log.info("결제 생성 시작: orderId={}, userId={}, loginId={}, orderStatus={}, amount={}",
                command.orderId(), command.userId(), command.loginId(), command.orderStatus(), command.amount());

        Payment existing = paymentRepository.findByOrderId(command.orderId()).orElse(null);
        if (existing != null) {
            log.warn("결제 생성 스킵(이미 결제 존재): orderId={}, paymentId={}, status={}, amount={}",
                    command.orderId(), existing.getId(), existing.getStatus(), command.amount());
            return CreatePaymentRes.from(existing);
        }

        OrderRes order = orderClient.getOrderByOrderId(command.orderId());
        UserRes user = userClient.getUserByLoginId(command.loginId());

        if (command.userId() != null && !command.userId().equals(order.getUserId())) {
            log.error("주문의 userId와 요청 userId가 일치하지 않음: orderUserId={}, requestUserId={}",
                    order.getUserId(), command.userId());
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }

        paymentValidator.validateCreatePayment(command, order, user);

        Payment payment = Payment.createPending(
                command.orderId(),
                user.getUserId(),
                command.amount()
        );

        paymentRepository.save(payment);
        paymentLogRepository.save(PaymentLog.createPendingLog(payment));

        log.info("결제 생성 완료 (PENDING 상태): paymentId={}, orderId={}, userId={}",
                payment.getId(), payment.getOrderId(), payment.getUserId());
        return CreatePaymentRes.from(payment);
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    /*
      결제 완료 처리
      - paymentKey를 받아서 Toss API 호출
      - Payment 상태를 APPROVED/FAILED로 업데이트
      - PaymentLog 결과는 상태에 따라 1번 저장
     */
    public CreatePaymentRes completePayment(CompletePaymentCommand command) {
        log.info("결제 완료 처리 시작: paymentId={}, paymentKey={}, loginId={}",
                command.paymentId(), command.paymentKey(), command.loginId());

        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (!payment.isPending()) {
            log.warn("결제 완료 처리 불가(이미 처리 완료 상태): paymentId={}, status={}",
                    payment.getId(), payment.getStatus());
            throw new BusinessException(PaymentErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        payment.updatePaymentKey(command.paymentKey());

        PGPaymentRes pgRes;
        try {
            log.info("Toss 결제 확인 요청: paymentId={}, orderId={}, amount={}",
                    payment.getId(), payment.getOrderId(), payment.getAmount());

            pgRes = pgPaymentService.requestPayment(payment);

            log.info("Toss 결제 확인 응답: paymentId={}, reason={}, pgMessage={}",
                    payment.getId(), pgRes.isSuccess(), pgRes.getPgResponseMessage());
        } catch (Exception e) {
            log.error("Toss 결제 확인 API 호출 실패: paymentId={}, paymentKey={}",
                    payment.getId(), command.paymentKey(), e);
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }

        if (pgRes.isSuccess()) {
            payment.approve(resolvePaymentKey(pgRes, payment));
            log.info("결제 완료 성공: paymentId={}, orderId={}", payment.getId(), payment.getOrderId());
        } else {
            payment.fail(pgRes.getPgResponseMessage());
            log.warn("결제 완료 실패: paymentId={}, reason={}", payment.getId(), pgRes.getPgResponseMessage());
        }

        paymentRepository.save(payment);

        PaymentLog resultLog = pgRes.isSuccess()
                ? PaymentLog.createApprovedLog(payment, pgRes)
                : PaymentLog.createFailedLog(payment, pgRes);
        paymentLogRepository.save(resultLog);

        if (!pgRes.isSuccess()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_FAILED);
        }

        orderClient.completeOrderPayment(
                payment.getOrderId(),
                payment.getId(),
                payment.getAmount()
        );

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

        PGPaymentRes pgRes;
        try {
            log.info("Toss 결제 취소 요청: paymentId={}, cancelAmount={}, reason={}",
                    payment.getId(), command.cancelAmount(), command.cancelReason());

            pgRes = pgPaymentService.cancelPayment(payment, command.cancelAmount(), command.cancelReason());

            log.info("Toss 결제 취소 응답: paymentId={}, success={}, pgMessage={}",
                    payment.getId(), pgRes.isSuccess(), pgRes.getPgResponseMessage());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Toss 결제 취소 API 호출 실패: paymentId={}", payment.getId(), e);
            throw new BusinessException(CommonErrorCode.FEIGN_ERROR);
        }

        if (pgRes.isSuccess()) {
            payment.cancel(command.cancelReason());
            paymentRepository.save(payment);

            log.info("[CANCEL] pgRes.success=false -> save cancelFailedLog start. paymentId={}", payment.getId());
            paymentLogRepository.save(PaymentLog.createCancelFailedLog(payment, pgRes));
            log.info("[CANCEL] save cancelFailedLog done. paymentId={}", payment.getId());

            log.info("결제 취소 완료: paymentId={}, status={}", payment.getId(), payment.getStatus());
            return CancelPaymentRes.from(payment);
        }

        paymentLogRepository.save(PaymentLog.createCancelFailedLog(payment, pgRes));

        log.warn("결제 취소 실패: paymentId={}, reason={}", payment.getId(), pgRes.getPgResponseMessage());
        throw new BusinessException(PaymentErrorCode.PAYMENT_CANCEL_FAILED);
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

        if (payment.isPending()) {
            payment.softDelete();
            paymentRepository.save(payment);
            log.info("결제 삭제 완료: paymentId={}", payment.getId());
        } else {
            throw new BusinessException(PaymentErrorCode.PAYMENT_CANNOT_BE_DELETED);
        }
    }

    private String resolvePaymentKey(PGPaymentRes pgRes, Payment payment) {
        return Objects.toString(pgRes.getPaymentKey(), Objects.toString(payment.getPaymentKey(), ""));
    }
}
