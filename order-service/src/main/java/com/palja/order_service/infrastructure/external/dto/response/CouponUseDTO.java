package com.palja.order_service.infrastructure.external.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponUseDTO {

    private UUID couponUserId;
    private UUID couponId;
    private Long userId;
    private UUID orderId;
    private LocalDateTime expireAt;
    private String status;              // UNUSED , USED, EXPIRE
    private LocalDateTime usedAt;
    private BigDecimal discountAmount;

    /**
     * TODO: 쿠폰 서비스 연동 전까지 사용하는 더미 데이터.
     *       coupon-service 연동 후 삭제 예정.
     */
    public static CouponUseDTO dummy(UUID couponId, UUID orderId) {
        return new CouponUseDTO(
                UUID.randomUUID(),
                couponId,
                1L,
                orderId,
                LocalDateTime.now().plusDays(30),
                "USED",
                LocalDateTime.now(),
                BigDecimal.valueOf(5000)
        );
    }
}