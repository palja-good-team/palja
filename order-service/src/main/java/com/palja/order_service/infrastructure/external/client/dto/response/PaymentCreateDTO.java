package com.palja.order_service.infrastructure.external.client.dto.response;

import com.palja.order_service.application.dto.external.PaymentCreateRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDTO {

    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;          // PENDING, APPROVED, FAILED, CANCELED
    private LocalDateTime requestedAt;

    public PaymentCreateRes toResponse() {
        return PaymentCreateRes.builder()
                .paymentId(paymentId)
                .amount(amount)
                .build();
    }
}
