package com.palja.coupon_service.application.event.dto.request;

import com.palja.coupon_service.application.event.OrderEvent;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponUseEventReq implements OrderEvent {

    private UUID sagaId;
    private UUID orderId;
    private UUID couponUserId;
    private BigDecimal discountAmount;

    public static CouponUseEventReq of(UUID sagaId, UUID orderId, UUID couponUserId, BigDecimal discountAmount) {
        return CouponUseEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .couponUserId(couponUserId)
                .discountAmount(discountAmount)
                .build();
    }
}
