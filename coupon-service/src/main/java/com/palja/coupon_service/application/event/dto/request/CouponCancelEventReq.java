package com.palja.coupon_service.application.event.dto.request;

import com.palja.coupon_service.application.event.OrderEvent;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponCancelEventReq implements OrderEvent {

    private UUID sagaId;
    private UUID orderId;
    private UUID couponUserId;

    public CouponCancelEventReq of(UUID sagaId, UUID orderId, UUID couponUserId) {
        return CouponCancelEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .couponUserId(couponUserId)
                .build();
    }

}
