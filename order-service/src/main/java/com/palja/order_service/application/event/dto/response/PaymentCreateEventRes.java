package com.palja.order_service.application.event.dto.response;

import com.palja.order_service.application.event.dto.SagaEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * 결제 생성 응답 이벤트
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateEventRes implements SagaEvent {
    private UUID sagaId;
    private UUID orderId;
    private UUID paymentId;
}
