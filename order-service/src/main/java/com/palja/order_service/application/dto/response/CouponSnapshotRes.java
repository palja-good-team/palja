package com.palja.order_service.application.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

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
