package com.palja.payment_service.infrastructure.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.application.service.PGPaymentService;
import com.palja.payment_service.domain.entity.Payment;
import com.palja.payment_service.infrastructure.dto.request.TossPaymentConfirmReq;
import com.palja.payment_service.infrastructure.dto.response.TossErrorRes;
import com.palja.payment_service.infrastructure.dto.response.TossPaymentRes;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossPaymentService implements PGPaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final ObjectMapper objectMapper;

    @Override
    public PGPaymentRes requestPayment(Payment payment) {
        TossPaymentConfirmReq req = TossPaymentConfirmReq.builder()
                .paymentKey(payment.getPaymentKey())
                .orderId(payment.getOrderId().toString())
                .amount(payment.getAmount())
                .build();

        try {
            TossPaymentRes res = tossPaymentClient.confirm(req);

            BigDecimal approvedAmount =
                    res.getTotalAmount() != null
                            ? BigDecimal.valueOf(res.getTotalAmount())
                            : null;

            boolean statusOk = "DONE".equalsIgnoreCase(res.getStatus());
            boolean amountOk = approvedAmount != null
                    && approvedAmount.compareTo(payment.getAmount()) == 0;

            boolean success = statusOk && amountOk;

            String message;
            if (!statusOk) {
                message = "토스 결제 상태가 DONE이 아닙니다. status=" + res.getStatus();
            } else if (!amountOk) {
                message = "토스 승인 금액과 요청 금액이 다릅니다. approved="
                        + approvedAmount + ", requested=" + payment.getAmount();
            } else {
                message = "토스 결제 승인 성공";
            }

            return PGPaymentRes.builder()
                    .paymentKey(res.getPaymentKey())
                    .pgResponseCode(success ? "SUCCESS" : res.getStatus())
                    .pgResponseMessage(message)
                    .success(success)
                    .approvedAmount(approvedAmount)
                    .build();

        } catch (FeignException e) {
            log.error("토스 결제 승인 API 실패. status={} body={}",
                    e.status(), e.contentUTF8(), e);

            String code = null;
            String message = null;

            try {
                TossErrorRes error = objectMapper.readValue(
                        e.contentUTF8(),
                        TossErrorRes.class
                );
                code = error.getCode();
                message = error.getMessage();
            } catch (Exception parseEx) {
                log.warn("토스 에러 응답 파싱 실패", parseEx);
            }

            return PGPaymentRes.builder()
                    .paymentKey(payment.getPaymentKey())
                    .pgResponseCode(code != null ? code : String.valueOf(e.status()))
                    .pgResponseMessage(message != null ? message : "토스 결제 승인 실패")
                    .success(false)
                    .approvedAmount(null)
                    .build();
        }
    }
}
