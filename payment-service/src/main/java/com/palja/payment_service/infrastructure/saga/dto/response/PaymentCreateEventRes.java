package com.palja.payment_service.infrastructure.saga.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateEventRes {
    private UUID sagaId;
    private UUID orderId;
    private UUID paymentId;
}
