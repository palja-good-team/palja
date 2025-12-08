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
public class CancelPaymentRes {

    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private String status;
    private String cancelReason;
    private LocalDateTime completedAt;

    public static CancelPaymentRes from(Payment payment) {
        return CancelPaymentRes.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .cancelReason(payment.getCancelReason())
                .completedAt(payment.getCompletedAt())
                .build();
    }
}
