package com.palja.payment_service.application.service.impl;

import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.application.service.PaymentSagaService;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.domain.entity.PaymentLog;
import com.palja.payment_service.domain.repository.PaymentLogRepository;
import com.palja.payment_service.domain.repository.PaymentRepository;
import com.palja.payment_service.domain.vo.PaymentStatus;
import com.palja.payment_service.exception.PaymentErrorCode;
import com.palja.payment_service.infrastructure.saga.dto.request.PaymentCancelEventReq;
import com.palja.payment_service.infrastructure.saga.dto.request.PaymentCreateEventReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSagaServiceImpl implements PaymentSagaService {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final PGPaymentService pgPaymentService;

    /*
      Saga 결제 생성 (PENDING 생성만)
      orderId 기준 멱등 처리
      Toss 호출 X, orderStatus CREATED만 허용
     */
    @Override
    @Transactional
    public UUID handleCreate(PaymentCreateEventReq req) {

        validateCreateReq(req);
        validateOrderStatusForSagaCreate(req.getOrderStatus());

        UUID sagaId = req.getSagaId();
        UUID orderId = req.getOrderId();

        var existingOpt = paymentRepository.findByOrderId(orderId);
        if (existingOpt.isPresent()) {
            var existing = existingOpt.get();
            log.warn("saga 결제 생성 멱등처리: sagaId={}, orderId={}, paymentId={}, status={}",
                    sagaId, orderId, existing.getId(), existing.getStatus());
            return existing.getId();
        }

        Payment payment = Payment.createPending(
                orderId,
                req.getUserId(),
                req.getAmount()
        );

        paymentRepository.save(payment);

        paymentLogRepository.save(PaymentLog.createPendingLog(payment));

        log.info("saga 결제 생성 완료: sagaId={}, orderId={}, paymentId={}, amount={}",
                sagaId, orderId, payment.getId(), payment.getAmount());

        return payment.getId();
    }

    /*
      Saga 결제 취소 (보상/주문취소)
     payment 상태에 따라 처리
     PENDING: 로그 남김
     APPROVED: PG 취소 호출 → 성공시 cancel + 로그, 실패시 cancelFailedLog + 예외
     CANCELED/FAILED: 멱등 처리
     */
    @Override
    @Transactional
    public void handleCancel(PaymentCancelEventReq req) {

        validateCancelReq(req);

        UUID sagaId = req.getSagaId();
        UUID orderId = req.getOrderId();
        UUID paymentId = req.getPaymentId();

        if (paymentId == null) {
            log.warn("saga 결제 취소: sagaId={}, orderId={}, reason=paymentId_null",
                    sagaId, orderId);
            return;
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("saga 결제 취소 실패 paymentId 존재하지 않음: sagaId={}, orderId={}, paymentId={}",
                            sagaId, orderId, paymentId);
                    return new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND);
                });

        if (payment.getStatus() == PaymentStatus.CANCELED
                || payment.getStatus() == PaymentStatus.FAILED) {
            log.warn("saga 결제 취소 멱등처리: sagaId={}, orderId={}, paymentId={}, status={}",
                    sagaId, orderId, paymentId, payment.getStatus());
            return;
        }

        if (payment.getStatus() == PaymentStatus.PENDING) {
            payment.softDelete();
            paymentRepository.save(payment);

            PaymentLog compensateLog = PaymentLog.builder()
                    .payment(payment)
                    .orderId(payment.getOrderId())
                    .userId(payment.getUserId())
                    .amount(payment.getAmount())
                    .status(PaymentStatus.PENDING)
                    .paymentKey(Objects.toString(payment.getPaymentKey(), ""))
                    .pgResponseCode("SAGA_CANCEL_BEFORE_APPROVAL")
                    .pgResponseMessage(Objects.toString(req.getReason(), "SAGA_CANCEL"))
                    .processedAt(LocalDateTime.now())
                    .build();

            paymentLogRepository.save(compensateLog);

            log.info("saga 결제 취소 성공: sagaId={}, orderId={}, paymentId={}",
                    sagaId, orderId, paymentId);
            return;
        }

        if (payment.getStatus() == PaymentStatus.APPROVED) {

            BigDecimal cancelAmount = req.getAmount();
            String reason = Objects.toString(req.getReason(), "SAGA_CANCEL");

            PGPaymentRes pgRes = pgPaymentService.cancelPayment(payment, cancelAmount, reason);

            if (pgRes != null && pgRes.isSuccess()) {
                payment.cancel(reason);
                paymentRepository.save(payment);

                paymentLogRepository.save(PaymentLog.createCanceledLog(payment, pgRes));

                log.info("saga 결제 취소 성공 승인: sagaId={}, orderId={}, paymentId={}",
                        sagaId, orderId, paymentId);
                return;
            }

            paymentLogRepository.save(PaymentLog.createCancelFailedLog(payment, pgRes));

            log.error("saga 결제 취소 pg사 실패: sagaId={}, orderId={}, paymentId={}, pgMsg={}",
                    sagaId, orderId, paymentId,
                    pgRes != null ? pgRes.getPgResponseMessage() : "null");

            throw new BusinessException(PaymentErrorCode.PAYMENT_CANCEL_FAILED);
        }

        log.error("saga 결제 취소 실패 유효하지 않은 결제 상태: sagaId={}, orderId={}, paymentId={}, status={}",
                sagaId, orderId, paymentId, payment.getStatus());

        throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
    }

    private void validateCreateReq(PaymentCreateEventReq req) {
        if (req == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
        if (req.getSagaId() == null || req.getOrderId() == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
        if (req.getUserId() == null) {
            throw new BusinessException(PaymentErrorCode.USER_NOT_FOUND);
        }
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
    }

    private void validateCancelReq(PaymentCancelEventReq req) {
        if (req == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }
        if (req.getSagaId() == null || req.getOrderId() == null) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_INFO);
        }

        if (req.getPaymentId() != null) {
            if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException(PaymentErrorCode.PAYMENT_EXCEED_AMOUNT);
            }
        }
    }

    /*
     Saga에서 넘어오는 주문 상태 검증
     orderStatus가 CREATED만 결제 생성 허용
     */
    private void validateOrderStatusForSagaCreate(String orderStatus) {
        if (orderStatus == null || orderStatus.isBlank()) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
        if (!"CREATED".equalsIgnoreCase(orderStatus)) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_STATUS);
        }
    }
}
