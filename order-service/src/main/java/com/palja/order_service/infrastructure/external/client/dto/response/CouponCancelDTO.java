package com.palja.order_service.infrastructure.external.client.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// 쿠폰 사용 취소 응답 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponCancelDTO {

    private UUID userCouponId;
    private UUID couponId;
    private String status;

    // TODO: coupon-service 연동 전까지 사용할 더미 응답.
    public static CouponCancelDTO dummy(UUID couponId, UUID userCouponId) {
        return new CouponCancelDTO(
                userCouponId,
                couponId,
                "ACTIVE"
        );
    }
}