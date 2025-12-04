package com.palja.order_service.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * 주문에 스냅샷으로 남길 쿠폰 정보
 * - couponId
 * - couponName
 * - 최종 할인 금액
 */
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CouponSnapshotRes {

    private UUID couponId;
    private String couponName;

    public static CouponSnapshotRes of(UUID couponId, String couponName) {
        return CouponSnapshotRes.builder()
                .couponId(couponId)
                .couponName(couponName)
                .build();
    }
}
