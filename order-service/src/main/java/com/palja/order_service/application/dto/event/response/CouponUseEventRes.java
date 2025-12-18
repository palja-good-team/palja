package com.palja.order_service.application.dto.event.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * 쿠폰 사용 응답 이벤트
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponUseEventRes {
    private UUID sagaId;
    private UUID orderId;
}
