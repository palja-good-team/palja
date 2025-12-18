package com.palja.order_service.application.dto.event.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 결제 생성 응답 이벤트
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelEventRes {
    private UUID sagaId;
    private UUID orderId;
}