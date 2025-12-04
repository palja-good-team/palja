package com.palja.coupon_service.presentation.dto.request;

import com.palja.coupon_service.application.command.CreateCouponCommand;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateCouponReq {

    @NotNull(message = "쿠폰명은 필수입니다.")
    @Size(max = 50, message = "쿠폰명은 최대 50자까지 입력 가능합니다.")
    private String couponName;

    private String description;

    @NotNull(message = "쿠폰 타입은 필수입니다.")
    private String discountType;

    @NotNull(message = "할인율은 필수입니다.")
    @Positive(message = "할인율은 0보다 커야합니다.")
    private Integer discountValue;

    @Positive(message = "발행 총 수량은 0보다 커야합니다.")
    private Integer totalQuantity;

    @Positive(message = "최대 할인율은 0보다 커야합니다.")
    private Integer maxDiscountAmount;

    @Positive(message = "최소 주문 금액은 0보다 커야합니다.")
    private Integer minOrderAmount;

    private LocalDateTime issueStartAt;

    @Future(message = "발급 종료일은 현재 시간 이후여야 합니다")
    private LocalDateTime issueEndAt;

    public static CreateCouponCommand of(CreateCouponReq request) {
        return CreateCouponCommand.builder()
                .couponName(request.getCouponName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .totalQuantity(request.getTotalQuantity())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderAmount(request.getMinOrderAmount())
                .issueStartAt(request.getIssueStartAt())
                .issueEndAt(request.getIssueEndAt())
                .build();
    }
}
