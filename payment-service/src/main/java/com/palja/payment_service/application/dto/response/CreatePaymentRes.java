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
public class CreatePaymentRes {

    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime requestedAt;

    public static CreatePaymentRes from(Payment payment) {
        return CreatePaymentRes.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod().name())
                .status(payment.getStatus().name())
                .requestedAt(payment.getRequestedAt())
                .build();
    }
}
