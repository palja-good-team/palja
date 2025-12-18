package com.palja.order_service.application.dto.event.request;

import com.palja.order_service.application.dto.event.OrderSagaEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 결제 취소 요청 이벤트
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PaymentCancelEventReq {

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
}