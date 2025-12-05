package com.palja.order_service.infrastructure.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {

    private UUID couponId;
    private String name;
    private String discountType;      // PERCENTAGE, FIXED
    private int discountValue;        // 20 (% 또는 정액 금액, 타입에 따라 의미 달라짐)
    private int totalQuantity;
    private int issuedQuantity;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minOrderAmount;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private Integer validityDays;     // 발급 후 N일
    private String status;            // ACTIVE, PAUSED, EXPIRED, DELETED
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // TODO: 쿠폰 서비스 연동 전까지 사용하는 더미 데이터. coupon-service 연결 후 삭제.
    public static CouponDTO dummy(UUID couponId) {
        return new CouponDTO(
                couponId,
                "블랙프라이데이 20% 할인",
                "PERCENTAGE",
                20,
                1000,
                150,
                BigDecimal.valueOf(30000),
                BigDecimal.valueOf(1000),
                LocalDateTime.of(2025, 12, 1, 0, 0),
                LocalDateTime.of(2025, 12, 10, 23, 59, 59),
                7,
                "ACTIVE",
                LocalDateTime.of(2025, 11, 30, 10, 0),
                LocalDateTime.of(2025, 11, 30, 15, 0),
                null
        );
    }
}
