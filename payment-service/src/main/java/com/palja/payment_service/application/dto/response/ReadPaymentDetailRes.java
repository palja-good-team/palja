package com.palja.payment_service.application.dto.response;

import com.palja.payment_service.domain.entity.Payment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ReadPaymentDetailRes {

    private UUID paymentId;
    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String currency;
    private String paymentKey;
    private String status;
    private String cancelReason;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    public static ReadPaymentDetailRes from(Payment payment) {
        return ReadPaymentDetailRes.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .currency(payment.getCurrency())
                .paymentKey(payment.getPaymentKey())
                .status(payment.getStatus().name())
                .cancelReason(payment.getCancelReason())
                .requestedAt(payment.getRequestedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }
}
