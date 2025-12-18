package com.palja.order_service.application.dto.event.request;

import com.palja.order_service.application.dto.event.OrderSagaEvent;
import lombok.*;

import java.util.UUID;

/**
 * 쿠폰 취소 요청 이벤트 (보상)
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class CouponCancelEventReq implements OrderSagaEvent {

    private final UUID sagaId;
    private final UUID orderId;

    private final UUID couponUserId;

    public static CouponCancelEventReq of(
            UUID sagaId,
            UUID orderId,
            UUID couponUserId
    ) {
        return CouponCancelEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .couponUserId(couponUserId)
                .build();
    }
}