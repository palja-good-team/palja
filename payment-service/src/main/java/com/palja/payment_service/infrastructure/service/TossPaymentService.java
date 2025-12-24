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

    @Override
    @Retryable(
            retryFor = {TransientPgException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 300, multiplier = 2.0, maxDelay = 1500)
    )
    public PGPaymentRes requestPayment(Payment payment) {
        String paymentKey = payment.getPaymentKey();
        log.info("T토스 결제 요청: paymentKey={}, orderId={}, amount={}",
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

            log.info("토스 결제 조회 응답: paymentKey={}, status={}, approvedAmount={}, success={}",
                    res.getPaymentKey(), res.getStatus(), approvedAmount, success);

            return PGPaymentRes.builder()
                    .paymentKey(res.getPaymentKey())
                    .pgResponseCode(success ? "SUCCESS" : res.getStatus())
                    .pgResponseMessage(message)
                    .success(success)
                    .approvedAmount(approvedAmount)
                    .build();

        } catch (WebClientRequestException e) {
            log.warn("토스 결제 조회 네트워크 오류(재시도): paymentKey={}, msg={}", paymentKey, e.getMessage());
            throw new TransientPgException("토스 결제 조회 네트워크 오류", e);

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                log.warn("토스 결제 조회 서버 오류(재시도): status={}, body={}", e.getStatusCode(), safeBody(e));
                throw new TransientPgException("토스 결제 조회 서버 오류", e);
            }

            log.error("토스 결제 조회 실패: status={}, body={}", e.getStatusCode(), safeBody(e), e);
            return PGPaymentRes.builder()
                    .paymentKey(paymentKey)
                    .pgResponseCode(String.valueOf(e.getStatusCode().value()))
                    .pgResponseMessage(safeBody(e))
                    .success(false)
                    .approvedAmount(null)
                    .build();
        }
    }

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

        log.info("토스 결제 취소 요청: paymentKey={}, orderId={}, cancelAmount={}, reason={}",
                paymentKey, payment.getOrderId(), cancelAmount, cancelReason);

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
            log.warn("토스 결제 취소 네트워크 오류(재시도): paymentKey={}, msg={}", paymentKey, e.getMessage());
            throw new TransientPgException("토스 결제 취소 네트워크 오류", e);

        } catch (WebClientResponseException e) {
            if (e.getStatusCode().is5xxServerError()) {
                log.warn("토스 결제 취소 서버 오류(재시도): status={}, body={}", e.getStatusCode(), safeBody(e));
                throw new TransientPgException("토스 결제 취소 서버 오류", e);
            }

            log.error("토스 결제 취소 실패: paymentKey={}, statusCode={}, body={}",
                    paymentKey, e.getStatusCode().value(), safeBody(e));

            return PGPaymentRes.builder()
                    .paymentKey(paymentKey)
                    .pgResponseCode(String.valueOf(e.getStatusCode().value()))
                    .pgResponseMessage(safeBody(e))
                    .success(false)
                    .approvedAmount(null)
                    .build();
        }
    }

    private String safeBody(WebClientResponseException e) {
        try {
            return e.getResponseBodyAsString();
        } catch (Exception ignored) {
            return "unknown";
        }
    }
}
