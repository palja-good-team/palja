package com.palja.coupon_service.application.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CouponUseEventRse {

    private UUID sagaId;
    private UUID orderId;

    public static CouponUseEventRse of(UUID sagaId, UUID orderId) {
        return CouponUseEventRse.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .build();
    }
}
