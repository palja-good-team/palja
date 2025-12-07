package com.palja.payment_service.application.dto.response;

import com.palja.payment_service.domain.entity.PaymentLog;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PaymentLogDetailRes {

    private UUID paymentLogId;
    private UUID paymentId;
    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private String paymentKey;
    private String pgResponseCode;
    private String pgResponseMessage;
    private LocalDateTime processedAt;

    public static PaymentLogDetailRes from(PaymentLog log) {
        return PaymentLogDetailRes.builder()
                .paymentLogId(log.getId())
                .paymentId(log.getPayment().getId())
                .orderId(log.getOrderId())
                .userId(log.getUserId())
                .amount(log.getAmount())
                .status(log.getStatus().name())
                .paymentKey(log.getPaymentKey())
                .pgResponseCode(log.getPgResponseCode())
                .pgResponseMessage(log.getPgResponseMessage())
                .processedAt(log.getProcessedAt())
                .build();
    }
}
