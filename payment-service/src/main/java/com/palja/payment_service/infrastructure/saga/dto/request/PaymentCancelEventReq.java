package com.palja.payment_service.infrastructure.saga.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancelEventReq {
    private UUID sagaId;
    private UUID orderId;

    private UUID paymentId;
    private BigDecimal amount;
    private String reason;
}
