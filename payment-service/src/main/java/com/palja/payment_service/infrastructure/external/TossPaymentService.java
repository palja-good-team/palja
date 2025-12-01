package com.palja.payment_service.infrastructure.external;

import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.infrastructure.dto.response.TossPaymentRes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService implements PGPaymentService {

    private final WebClient tossWebClient;

    @Override
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
                message = "토스 결제 조회 성공";
            }

            log.info("Toss getPayment response. paymentKey={}, status={}, totalAmount={}",
                    res.getPaymentKey(), res.getStatus(), res.getTotalAmount());

            return PGPaymentRes.builder()
                    .paymentKey(res.getPaymentKey())
                    .pgResponseCode(success ? "SUCCESS" : res.getStatus())
                    .pgResponseMessage(message)
                    .success(success)
                    .approvedAmount(approvedAmount)
                    .build();

        } catch (WebClientResponseException e) {
            log.error("토스 결제 조회 API 실패. 상태 코드: {} 응답: {}",
                    e.getStatusCode(), e.getResponseBodyAsString(), e);
            return PGPaymentRes.builder()
                    .paymentKey(paymentKey)
                    .pgResponseCode(String.valueOf(e.getStatusCode()))
                    .pgResponseMessage(e.getResponseBodyAsString())
                    .success(false)
                    .approvedAmount(null)
                    .build();
        }
    }
}
