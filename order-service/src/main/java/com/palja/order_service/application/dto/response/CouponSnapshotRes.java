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

    private UUID couponUserId;
    private String couponName;

    public static CouponSnapshotRes of(UUID couponUserId, String couponName) {
        return CouponSnapshotRes.builder()
                .couponUserId(couponUserId)
                .couponName(couponName)
                .build();
    }
}
