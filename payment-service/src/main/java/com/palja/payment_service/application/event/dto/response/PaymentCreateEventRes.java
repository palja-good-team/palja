package com.palja.payment_service.application.event.dto.response;

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
