package com.palja.order_service.application.event.dto.request;

import com.palja.order_service.application.event.dto.SagaEvent;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * 쿠폰 취소 요청 이벤트 (보상)
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class CouponCancelEventReq implements SagaEvent {

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


    // OrderCanceledEventReq 기반 쿠폰 취소 이벤트 생성
    public static CouponCancelEventReq from(OrderCanceledEventReq canceledEvent) {
        return CouponCancelEventReq.builder()
                .sagaId(canceledEvent.getOrderId())
                .orderId(canceledEvent.getOrderId())
                .couponUserId(canceledEvent.getCouponUserId())
                .build();
    }
}