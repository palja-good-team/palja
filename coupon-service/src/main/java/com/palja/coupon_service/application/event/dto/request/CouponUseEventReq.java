package com.palja.coupon_service.application.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CouponUseEventReq {

    private final UUID sagaId;
    private final UUID orderId;
    private final UUID couponUserId;
    private final BigDecimal discountAmount;

    public static CouponUseEventReq of(UUID sagaId, UUID orderId, UUID couponUserId, BigDecimal discountAmount) {
        return CouponUseEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .couponUserId(couponUserId)
                .discountAmount(discountAmount)
                .build();
    }
}
