package com.palja.coupon_service.presentation.dto.request;

import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ChangeCouponStatusReq {
    @NotNull(message = "변경할 상태를 입력해주세요.")
    @Schema(
            description = "쿠폰 상태",
            allowableValues = {"ACTIVE", "INACTIVE", "EXPIRED"},
            example = "ACTIVE"
    )
    private String status;

    public static ChangeCouponStatusCommand of(UUID couponId, ChangeCouponStatusReq request) {
        return ChangeCouponStatusCommand.builder()
                .couponId(couponId)
                .status(request.getStatus())
                .build();
    }

    public static ChangeCouponStatusCommand of(UUID couponId, String userId, ChangeCouponStatusReq request) {
        return ChangeCouponStatusCommand.builder()
                .couponId(couponId)
                .userId(userId)
                .status(request.getStatus())
                .build();
    }
}
