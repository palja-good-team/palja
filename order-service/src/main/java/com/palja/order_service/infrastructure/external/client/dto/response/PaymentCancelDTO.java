package com.palja.order_service.infrastructure.external.client.dto.response;

import com.palja.order_service.application.dto.external.PaymentCancelRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelDTO {

    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private String status;
    private String cancelReason;
    private LocalDateTime completedAt;

    public PaymentCancelRes toResponse() {
        return PaymentCancelRes.builder()
                .paymentId(paymentId)
                .amount(amount)
                .build();
    }
}