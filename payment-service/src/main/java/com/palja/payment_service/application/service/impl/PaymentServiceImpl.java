package com.palja.payment_service.application.service.impl;

import com.palja.payment_service.domain.event.vo.PaymentEventType;
import io.micrometer.tracing.Tracer;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.palja.payment_service.application.event.dto.PaymentEventEnvelope;
import com.palja.payment_service.application.event.dto.request.PaymentApprovedEventReq;
import com.palja.payment_service.application.event.dto.request.PaymentCancelFailedEventReq;
import com.palja.payment_service.application.event.dto.request.PaymentCanceledEventReq;
import com.palja.payment_service.application.event.dto.request.PaymentFailedEventReq;
import com.palja.payment_service.application.port.OrderClient;
import com.palja.payment_service.application.port.UserClient;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.application.service.PaymentService;
import com.palja.payment_service.application.validator.PaymentValidator;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.entity.PaymentOutbox;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.repository.PaymentOutboxRepository;
import com.palja.payment_service.domain.repository.PaymentRepository;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final PaymentOutboxRepository paymentOutboxRepository;

    private final PGPaymentService pgPaymentService;
    private final PaymentValidator paymentValidator;

    private final OrderClient orderClient;
    private final UserClient userClient;

    private final ObjectMapper objectMapper;
    private final Tracer tracer;

    /*
      결제 생성 (PENDING 상태)
      - 결제 엔티티만 생성하고 PENDING 상태로 저장
      - Toss API 호출은 하지 않음
     */
    @Override
    @Transactional
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

    /*
      결제 완료 처리
      - paymentKey를 받아서 Toss API 호출
      - Payment 상태를 APPROVED/FAILED로 업데이트
      - PaymentLog 결과는 상태에 따라 1번 저장
      - 기존 FeignClient 호출하는 로직 제거, Outbox 적재(성공/실패 모두)
     */
    @Override
    @Transactional(noRollbackFor = BusinessException.class)
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

            log.info("Toss 결제 확인 응답: paymentId={}, success={}, pgMessage={}",
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

        if (pgRes.isSuccess()) {
            enqueueOutboxApproved(payment, pgRes);
        } else {
            enqueueOutboxFailed(payment, pgRes);
        }

        if (!pgRes.isSuccess()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_FAILED);
        }

        log.info("결제 완료 처리 종료: paymentId={}, userId={}", payment.getId(), payment.getUserId());
        return CreatePaymentRes.from(payment);
    }

    /*
        결제 취소 처리
        - Toss 취소 성공 시 payment.cancel + createCanceledLog + outbox PAYMENT_CANCELED
        - Toss 취소 실패 시 payment 상태 APPROVED 유지 + createCancelFailedLog + outbox PAYMENT_CANCEL_FAILED
     */
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

            paymentLogRepository.save(PaymentLog.createCanceledLog(payment, pgRes));
            enqueueOutboxCanceled(payment, command, pgRes);

            log.info("결제 취소 완료: paymentId={}, status={}", payment.getId(), payment.getStatus());
            return CancelPaymentRes.from(payment);
        }

        paymentLogRepository.save(PaymentLog.createCancelFailedLog(payment, pgRes));
        enqueueOutboxCancelFailed(payment, command, pgRes);

        log.warn("결제 취소 실패: paymentId={}, reason={}", payment.getId(), pgRes.getPgResponseMessage());
        throw new BusinessException(PaymentErrorCode.PAYMENT_CANCEL_FAILED);
    }

    @Override
    @Transactional(readOnly = true)
    public ReadPaymentDetailRes getPayment(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

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

    /*
        outbox enqueue
     */
    private void enqueueOutboxApproved(Payment payment, PGPaymentRes pgRes) {
        PaymentApprovedEventReq payload = PaymentApprovedEventReq.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .paidAmount(payment.getAmount())
                .paymentKey(payment.getPaymentKey())
                .pgResponseCode(pgRes.getPgResponseCode())
                .pgResponseMessage(pgRes.getPgResponseMessage())
                .approvedAt(LocalDateTime.now())
                .build();

        enqueueOutbox(payment, PaymentEventType.PAYMENT_APPROVED, payload);
    }

    private void enqueueOutboxFailed(Payment payment, PGPaymentRes pgRes) {
        PaymentFailedEventReq payload = PaymentFailedEventReq.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentKey(payment.getPaymentKey())
                .pgResponseCode(pgRes.getPgResponseCode())
                .pgResponseMessage(pgRes.getPgResponseMessage())
                .failedAt(LocalDateTime.now())
                .build();

        enqueueOutbox(payment, PaymentEventType.PAYMENT_FAILED, payload);
    }

    private void enqueueOutboxCanceled(Payment payment, CancelPaymentCommand command, PGPaymentRes pgRes) {
        PaymentCanceledEventReq payload = PaymentCanceledEventReq.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .canceledAmount(command.cancelAmount())
                .cancelReason(command.cancelReason())
                .paymentKey(payment.getPaymentKey())
                .pgResponseCode(pgRes.getPgResponseCode())
                .pgResponseMessage(pgRes.getPgResponseMessage())
                .canceledAt(LocalDateTime.now())
                .build();

        enqueueOutbox(payment, PaymentEventType.PAYMENT_CANCELED, payload);
    }

    private void enqueueOutboxCancelFailed(Payment payment, CancelPaymentCommand command, PGPaymentRes pgRes) {
        PaymentCancelFailedEventReq payload = PaymentCancelFailedEventReq.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .cancelReason(command.cancelReason())
                .paymentKey(payment.getPaymentKey())
                .pgResponseCode(pgRes.getPgResponseCode())
                .pgResponseMessage(pgRes.getPgResponseMessage())
                .failedAt(LocalDateTime.now())
                .build();

        enqueueOutbox(payment, PaymentEventType.PAYMENT_CANCEL_FAILED, payload);
    }

    private void enqueueOutbox(Payment payment, PaymentEventType type, Object payloadObj) {
        try {
            UUID eventId = UUID.randomUUID();

            String payloadJson = objectMapper.writeValueAsString(payloadObj);

            PaymentEventEnvelope envelope = PaymentEventEnvelope.builder()
                    .eventId(eventId)
                    .type(type)
                    .occurredAt(LocalDateTime.now())
                    .paymentId(payment.getId())
                    .orderId(payment.getOrderId())
                    .payloadJson(payloadJson)
                    .build();

            String envelopeJson = objectMapper.writeValueAsString(envelope);

            String loginId = null;
            UserRole userRole = null;

            try {
                loginId = CurrentUser.getLoginId();
                userRole = CurrentUser.getRole();
            } catch (Exception ignored) {
            }

            String traceId = null;
            String spanId = null;
            if (tracer != null && tracer.currentSpan() != null) {
                traceId = tracer.currentSpan().context().traceId();
                spanId = tracer.currentSpan().context().spanId();
            }

            paymentOutboxRepository.save(
                    PaymentOutbox.pending(
                            eventId,
                            payment.getId(),
                            payment.getOrderId(),
                            type,
                            envelopeJson,
                            loginId,
                            userRole,
                            traceId,
                            spanId
                    )
            );
        } catch (Exception e) {
            log.error("Outbox enqueue 실패: type={}, paymentId={}, orderId={}",
                    type, payment.getId(), payment.getOrderId(), e);
            throw new RuntimeException("enqueueOutbox failed. type=" + type, e);
        }
    }
}
