package com.palja.coupon_service.presentation.dto.request;

import com.palja.coupon_service.application.command.ChangeCouponStatusCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ChangeCouponStatusReq {
    @NotBlank(message = "변경할 상태를 입력해주세요.")
    @Schema(
            description = """
                    상태값
                    [쿠폰 상태]
                    - ACTIVE: 활성
                    - PAUSED: 일시정지
                    - EXPIRED: 만료
                    - DELETED: 삭제
                    
                    [사용자 쿠폰 상태]
                    - ISSUED: 발행됨
                    - USED: 사용 완료
                    - EXPIRED: 만료됨
                    """,
            allowableValues = {"ACTIVE", "PAUSED", "EXPIRED", "DELETED", "ISSUED", "USED", "EXPIRED"},
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
