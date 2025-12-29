package com.palja.coupon_service.application.event.dto.response;

import com.palja.coupon_service.application.event.OrderEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponCancelEventRes implements OrderEvent {

    private UUID sagaId;
    private UUID orderId;

    public static CouponCancelEventRes of(UUID sagaId, UUID orderId) {
        return CouponCancelEventRes.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .build();
    }

}
