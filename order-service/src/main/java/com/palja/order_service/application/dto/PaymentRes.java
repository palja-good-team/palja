package com.palja.order_service.application.dto;

import com.palja.order_service.infrastructure.external.dto.response.PaymentDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRes {

    private final UUID paymentId;
    private final UUID orderId;
    private final Long userId;
    private final BigDecimal amount;
    private final String paymentMethod;
    private final String currency;
    private final String paymentKey;
    private final String status;
    private final LocalDateTime requestedAt;
    private final LocalDateTime completedAt;

    // Infrastructure DTO → Application DTO 변환
    public static PaymentRes from(PaymentDTO paymentDTO) {
        return PaymentRes.builder()
                .paymentId(paymentDTO.getPaymentId())
                .orderId(paymentDTO.getOrderId())
                .userId(paymentDTO.getUserId())
                .amount(paymentDTO.getAmount())
                .paymentMethod(paymentDTO.getPaymentMethod())
                .currency(paymentDTO.getCurrency())
                .paymentKey(paymentDTO.getPaymentKey())
                .status(paymentDTO.getStatus())
                .requestedAt(paymentDTO.getRequestedAt())
                .completedAt(paymentDTO.getCompletedAt())
                .build();
    }
}