package com.palja.coupon_service.presentation.dto.request;

import com.palja.coupon_service.application.command.UseCouponCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UseCouponReq {

    @NotNull(message = "주문 ID는 필수입니다.")
    private UUID orderId;

    @Positive(message = "할인 금액은 0보다 커야합니다.")
    private Long discountAmount;

    public static UseCouponCommand of (UUID couponUserId, String userId, UseCouponReq request) {
        return UseCouponCommand.builder()
                .couponUserId(couponUserId)
                .userId(userId)
                .orderId(request.getOrderId())
                .discountAmount(request.getDiscountAmount())
                .build();
    }
}
