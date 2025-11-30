package com.palja.payment_service.application.dto.response;

import com.palja.payment_service.domain.entity.Payment;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PaymentDetailRes {

    private UUID paymentId;
    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String currency;
    private String paymentKey;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    public static PaymentDetailRes from (Payment payment) {
        return PaymentDetailRes.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .currency(payment.getCurrency())
                .paymentKey(payment.getPaymentKey())
                .status(payment.getStatus().name())
                .requestedAt(payment.getRequestedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }
}
