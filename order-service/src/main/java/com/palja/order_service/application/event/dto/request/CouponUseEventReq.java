package com.palja.order_service.application.event.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 쿠폰 사용 요청 이벤트
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class CouponUseEventReq {

    private final UUID sagaId;
    private final UUID orderId;

    private final UUID couponUserId;
    private final BigDecimal discountAmount;

    public static CouponUseEventReq of(
            UUID sagaId,
            UUID orderId,
            UUID couponUserId,
            BigDecimal discountAmount
    ) {
        return CouponUseEventReq.builder()
                .sagaId(sagaId)
                .orderId(orderId)
                .couponUserId(couponUserId)
                .discountAmount(discountAmount)
                .build();
    }
}