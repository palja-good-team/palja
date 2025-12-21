package com.palja.order_service.application.event.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * 쿠폰 사용 응답 이벤트
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponUseEventRes {
    private UUID sagaId;
    private UUID orderId;
}
