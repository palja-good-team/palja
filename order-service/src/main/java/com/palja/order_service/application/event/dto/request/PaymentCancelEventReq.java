package com.palja.order_service.application.event.dto.request;

import com.palja.order_service.application.event.dto.SagaEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 결제 취소 요청 이벤트
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PaymentCancelEventReq implements SagaEvent {

    private final UUID sagaId;
    private final UUID orderId;

    private final UUID paymentId;
    private final BigDecimal amount;
    private final String reason;

    public static PaymentCancelEventReq of(
            UUID sagaId,
            UUID orderId,
            UUID paymentId,
            BigDecimal amount,
            String reason
    ) {
        return PaymentCancelEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .paymentId(paymentId)
                .amount(amount)
                .reason(reason)
                .build();
    }

    // OrderCanceledEventReq 기반 결제 취소 이벤트 생성
    public static PaymentCancelEventReq from(OrderCanceledEventReq canceledEvent) {
        return PaymentCancelEventReq.builder()
                .sagaId(canceledEvent.getOrderId())
                .orderId(canceledEvent.getOrderId())
                .paymentId(canceledEvent.getPaymentId())
                .amount(canceledEvent.getAmount())
                .reason(canceledEvent.getCancelReason())
                .build();
    }
}