package com.palja.order_service.application.event.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 결제 생성 요청 이벤트
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PaymentCreateEventReq {

    private final UUID sagaId;
    private final UUID orderId;

    private final Long userId;
    private final BigDecimal amount;
    private final String orderStatus;

    public static PaymentCreateEventReq of(
            UUID sagaId,
            UUID orderId,
            Long userId,
            BigDecimal amount,
            String orderStatus
    ) {
        return PaymentCreateEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .userId(userId)
                .amount(amount)
                .orderStatus(orderStatus)
                .build();
    }
}