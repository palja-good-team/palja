package com.palja.coupon_service.presentation.dto.request;

import com.palja.coupon_service.application.command.UpdateCouponCommand;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCouponReq {

    @Size(max = 50, message = "쿠폰명은 최대 50자까지 입력 가능합니다.")
    private String couponName;

    private String description;

    @Positive(message = "발행 총 수량은 0보다 커야합니다.")
    private Integer totalQuantity;

    @Positive(message = "최대 할인율은 0보다 커야합니다.")
    private Integer maxDiscountAmount;

    @Positive(message = "최소 주문 금액은 0보다 커야합니다.")
    private Integer minOrderAmount;

    private LocalDateTime issueStartAt;

    @Future(message = "발급 종료일은 현재 시간 이후여야 합니다")
    private LocalDateTime issueEndAt;

    public static UpdateCouponCommand of(UUID couponId, UpdateCouponReq request) {
        return UpdateCouponCommand.builder()
                .couponId(couponId)
                .couponName(request.getCouponName())
                .description(request.getDescription())
                .totalQuantity(request.getTotalQuantity())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderAmount(request.getMinOrderAmount())
                .issueStartAt(request.getIssueStartAt())
                .issueEndAt(request.getIssueEndAt())
                .build();
    }
}