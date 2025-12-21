package com.palja.coupon_service.application.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CouponCancelEventReq {

    private final UUID sagaId;
    private final UUID orderId;
    private final UUID couponUserId;

    public CouponCancelEventReq of(UUID sagaId, UUID orderId, UUID couponUserId) {
        return CouponCancelEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .couponUserId(couponUserId)
                .build();
    }

}
