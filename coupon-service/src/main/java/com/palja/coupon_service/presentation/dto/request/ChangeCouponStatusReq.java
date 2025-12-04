package com.palja.coupon_service.presentation.dto.request;

import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import com.palja.coupon_service.domain.vo.CouponStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ChangeCouponStatusReq {
    @NotNull(message = "변경할 상태를 입력해주세요.")
    private String status;

    public static ChangeCouponStatusCommand of(UUID couponId, ChangeCouponStatusReq request) {
        return ChangeCouponStatusCommand.builder()
                .couponId(couponId)
                .status(CouponStatus.valueOf(request.getStatus().toUpperCase()))
                .build();
    }
}
