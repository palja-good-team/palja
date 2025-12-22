package com.palja.payment_service.application.event.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCreateEventReq {
    private UUID sagaId;
    private UUID orderId;

    private Long userId;
    private BigDecimal amount;
    private String orderStatus;
}
