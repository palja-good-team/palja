package com.palja.order_service.application.dto.event.response;

import lombok.*;

import java.util.UUID;

/**
 * 결제 생성 응답 이벤트
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateEventRes {
    private UUID sagaId;
    private UUID orderId;
    private UUID paymentId;
}
