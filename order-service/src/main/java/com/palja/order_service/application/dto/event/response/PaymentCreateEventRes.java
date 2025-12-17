package com.palja.order_service.application.dto.event.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 결제 생성 응답 이벤트
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateEventRes {
    private UUID sagaId;
    private UUID orderId;
    private UUID paymentId;
    private boolean success;
    private String errorMessage;

    private UUID correlationId;
    private Long timestamp;
}
