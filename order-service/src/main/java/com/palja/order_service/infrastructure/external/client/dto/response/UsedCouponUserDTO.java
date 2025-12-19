package com.palja.order_service.infrastructure.external.client.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class UsedCouponUserDTO {
    private UUID couponUserId;
    private UUID couponId;
    private String userId;
    private String couponName;
    private String status;
    private LocalDateTime usedAt;
    private UUID orderId;
    private Long discountAmount;
}