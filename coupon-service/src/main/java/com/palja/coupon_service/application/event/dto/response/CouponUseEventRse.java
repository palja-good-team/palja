package com.palja.coupon_service.application.event.dto.response;

import com.palja.coupon_service.application.event.OrderEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponUseEventRse implements OrderEvent {

    private UUID sagaId;
    private UUID orderId;

    public static CouponUseEventRse of(UUID sagaId, UUID orderId) {
        return CouponUseEventRse.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .build();
    }
}
