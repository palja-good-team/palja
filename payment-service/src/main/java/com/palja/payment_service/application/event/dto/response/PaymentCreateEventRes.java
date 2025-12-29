package com.palja.payment_service.application.event.dto.response;

import com.palja.payment_service.application.event.dto.OrderEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateEventRes implements OrderEvent {
    private UUID sagaId;
    private UUID orderId;
    private UUID paymentId;
}
