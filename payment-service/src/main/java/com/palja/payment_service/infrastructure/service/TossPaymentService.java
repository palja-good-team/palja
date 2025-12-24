package com.palja.payment_service.infrastructure.service;

import com.palja.common.exception.BusinessException;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.exception.PaymentErrorCode;
import com.palja.payment_service.infrastructure.dto.request.TossPaymentCancelReq;
import com.palja.payment_service.infrastructure.dto.response.TossPaymentRes;
import com.palja.payment_service.infrastructure.service.exception.TransientPgException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService implements PGPaymentService {

    private final WebClient tossWebClient;

    /**
     * ✅ PG 조회(결제 확인) - "일시적 오류"에 대해서만 짧게 재시도
     *
     * - 재시도 대상: 네트워크 오류(WebClientRequestException), 5xx(WebClientResponseException의 5xx)
     * - 비재시도: 4xx는 즉시 실패 응답 반환
     *
     * 중요: 여기서 retry는 "한 번의 요청" 내부에서만 발생.
     */
    @Override
    @Retryable(
            retryFor = {TransientPgException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 300, multiplier = 2.0, maxDelay = 1500)
    )
    public PGPaymentRes requestPayment(Payment payment) {
        String paymentKey = payment.getPaymentKey();
        log.info("Toss getPayment request. paymentKey={}, orderId={}, amount={}",
                paymentKey, payment.getOrderId(), payment.getAmount());

        try {
            TossPaymentRes res = tossWebClient.get()
                    .uri("/v1/payments/{paymentKey}", paymentKey)
                    .retrieve()
                    .bodyToMono(TossPaymentRes.class)
                    .block();

            BigDecimal approvedAmount = res.getTotalAmount() != null
                    ? BigDecimal.valueOf(res.getTotalAmount())
                    : null;

            boolean statusOk = "DONE".equalsIgnoreCase(res.getStatus());
            boolean amountOk = approvedAmount != null && approvedAmount.compareTo(payment.getAmount()) == 0;

            boolean success = statusOk && amountOk;

            String message;
            if (!statusOk) {
                message = "토스 결제 상태가 DONE이 아닙니다. status=" + res.getStatus();
            } else if (!amountOk) {
                message = "토스 승인 금액과 요청 금액이 다릅니다. approved=" + approvedAmount + ", requested=" + payment.getAmount();
            } else {
                message = "토스 결제 성공";
            }

            return PGPaymentRes.builder()
                    .paymentKey(res.getPaymentKey())
                    .pgResponseCode(success ? "SUCCESS" : res.getStatus())
                    .pgResponseMessage(message)
                    .success(success)
                    .approvedAmount(approvedAmount)
                    .build();

        } catch (WebClientRequestException e) {
            // ✅ 네트워크 오류 => 재시도 대상
            log.warn("Toss getPayment transient network error. paymentKey={}, msg={}", paymentKey, e.getMessage());
            throw new TransientPgException("Transient network error while requesting Toss payment", e);

        } catch (WebClientResponseException e) {
            // ✅ 5xx만 재시도, 4xx는 즉시 실패 처리
            if (e.getStatusCode().is5xxServerError()) {
                log.warn("Toss getPayment transient 5xx. status={}, body={}", e.getStatusCode(), safeBody(e));
                throw new TransientPgException("Transient 5xx from Toss getPayment", e);
            }

            log.error("Toss getPayment 4xx/other. status={}, body={}", e.getStatusCode(), safeBody(e), e);
            return PGPaymentRes.builder()
                    .paymentKey(paymentKey)
                    .pgResponseCode(String.valueOf(e.getStatusCode().value()))
                    .pgResponseMessage(safeBody(e))
                    .success(false)
                    .approvedAmount(null)
                    .build();
        }
    }

    /**
     * ✅ PG 취소 - "일시적 오류"에 대해서만 짧게 재시도
     */
    @Override
    @Retryable(
            retryFor = {TransientPgException.class},
            maxAttempts = 2,
            backoff = @Backoff(delay = 300, multiplier = 2.0, maxDelay = 1500)
    )
    public PGPaymentRes cancelPayment(Payment payment, BigDecimal cancelAmount, String cancelReason) {
        String paymentKey = payment.getPaymentKey();

        BigDecimal paymentAmount = payment.getAmount();
        if (cancelAmount.compareTo(paymentAmount) > 0) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_EXCEED_AMOUNT);
        }
        if (cancelAmount.compareTo(paymentAmount) < 0) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_PARTIAL_REFUND);
        }

        try {
            TossPaymentCancelReq req = TossPaymentCancelReq.builder()
                    .cancelAmount(cancelAmount)
                    .cancelReason(cancelReason)
                    .build();

            TossPaymentRes res = tossWebClient.post()
                    .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                    .bodyValue(req)
                    .retrieve()
                    .bodyToMono(TossPaymentRes.class)
                    .block();

            boolean success = "CANCELED".equalsIgnoreCase(res.getStatus());
            String message = success ? "토스 결제 취소 성공" : "토스 결제 취소 실패. status=" + res.getStatus();

            return PGPaymentRes.builder()
                    .paymentKey(res.getPaymentKey())
                    .pgResponseCode(success ? "SUCCESS" : res.getStatus())
                    .pgResponseMessage(message)
                    .success(success)
                    .approvedAmount(null)
                    .build();

        } catch (WebClientRequestException e) {
            log.warn("Toss cancel transient network error. paymentKey={}, msg={}", paymentKey, e.getMessage());
            throw new TransientPgException("Transient network error while canceling Toss payment", e);

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                log.warn("Toss cancel transient 5xx. status={}, body={}", e.getStatusCode(), safeBody(e));
                throw new TransientPgException("Transient 5xx from Toss cancel", e);
            }

            log.error("Toss cancel 4xx/other. status={}, body={}", e.getStatusCode(), safeBody(e), e);
            return PGPaymentRes.builder()
                    .paymentKey(paymentKey)
                    .pgResponseCode(String.valueOf(e.getStatusCode().value()))
                    .pgResponseMessage(safeBody(e))
                    .success(false)
                    .approvedAmount(null)
                    .build();
        }
    }

    @Recover
    public PGPaymentRes recover(TransientPgException ex, Payment payment) {
        String paymentKey = payment.getPaymentKey();
        log.error("Toss getPayment retry exhausted. paymentId={}, paymentKey={}, msg={}",
                payment.getId(), paymentKey, ex.getMessage(), ex);

        return PGPaymentRes.builder()
                .paymentKey(paymentKey)
                .pgResponseCode("RETRY_EXHAUSTED")
                .pgResponseMessage("PG 일시 장애로 요청 재시도 실패: " + ex.getMessage())
                .success(false)
                .approvedAmount(null)
                .build();
    }

    @Recover
    public PGPaymentRes recover(TransientPgException ex, Payment payment, BigDecimal cancelAmount, String cancelReason) {
        String paymentKey = payment.getPaymentKey();
        log.error("Toss cancel retry exhausted. paymentId={}, paymentKey={}, msg={}",
                payment.getId(), paymentKey, ex.getMessage(), ex);

        return PGPaymentRes.builder()
                .paymentKey(paymentKey)
                .pgResponseCode("RETRY_EXHAUSTED")
                .pgResponseMessage("PG 일시 장애로 취소 요청 재시도 실패: " + ex.getMessage())
                .success(false)
                .approvedAmount(null)
                .build();
    }

    private String safeBody(WebClientResponseException e) {
        try {
            return e.getResponseBodyAsString();
        } catch (Exception ignored) {
            return "unknown";
        }
    }
}
