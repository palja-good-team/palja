package com.palja.coupon_service.presentation.dto.request;

import com.palja.coupon_service.application.command.CreateCouponCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CreateCouponReq {

    @NotBlank(message = "쿠폰명은 필수입니다.")
    @Size(max = 50, message = "쿠폰명은 최대 50자까지 입력 가능합니다.")
    @Schema(description = "쿠폰명", example = "신규회원 환영 쿠폰")
    private String couponName;

    @Schema(description = "쿠폰 설명", example = "신규 가입 회원에게 제공되는 할인 쿠폰입니다")
    private String description;

    @NotBlank(message = "쿠폰 할인 타입은 필수입니다.")
    @Schema(description = "할인 타입", allowableValues = {"PERCENTAGE", "FIXED"}, example = "PERCENTAGE")
    private String discountType;

    @NotNull(message = "할인율은 필수입니다.")
    @Positive(message = "할인율은 0보다 커야합니다.")
    @Schema(description = "할인율", defaultValue = "10")
    private Integer discountValue;

    @Positive(message = "발행 총 수량은 0보다 커야합니다.")
    @Schema(description = "발행 총 수량", example = "1000")
    private Integer totalQuantity;

    @Positive(message = "최대 할인율은 0보다 커야합니다.")
    @Schema(description = "최대 할인율", example = "10000")
    private Integer maxDiscountAmount;

    @Positive(message = "최소 주문 금액은 0보다 커야합니다.")
    @Schema(description = "최소 주문 금액", example = "10000")
    private Integer minOrderAmount;

    @Schema(description = "발행 시작일")
    private LocalDateTime issueStartAt;

    @Future(message = "발급 종료일은 현재 시간 이후여야 합니다")
    @Schema(description = "발행 종료일")
    private LocalDateTime issueEndAt;

    @NotNull(message = "사용 기간은 필수입니다.")
    @Positive(message = "사용 기간은 0보다 커야합니다.")
    @Schema(description = "사용 기간", defaultValue = "7")
    private Integer usageDays;

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
                .usageDays(request.getUsageDays())
                .build();
    }
}
