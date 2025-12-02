package com.palja.payment_service.presentation.dto.response;

import com.palja.payment_service.application.dto.response.PaymentDetailRes;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PaymentRes {
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

    public static PaymentRes from(PaymentDetailRes dto) {
        return PaymentRes.builder()
                .paymentId(dto.getPaymentId())
                .orderId(dto.getOrderId())
                .userId(dto.getUserId())
                .amount(dto.getAmount())
                .paymentMethod(dto.getPaymentMethod())
                .currency(dto.getCurrency())
                .paymentKey(dto.getPaymentKey())
                .status(dto.getStatus())
                .requestedAt(dto.getRequestedAt())
                .completedAt(dto.getCompletedAt())
                .build();
    }
}
