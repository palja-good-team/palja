package com.palja.coupon_service.presentation.dto.request;

import com.palja.coupon_service.application.command.CreateCouponCommand;
import com.palja.coupon_service.domain.vo.DiscountType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateCouponReq {

    @NotNull(message = "쿠폰명은 필수입니다.")
    @Size(max = 50, message = "쿠폰명은 최대 50자까지 입력 가능합니다.")
    private String couponName;

    private String description;

    @NotNull(message = "쿠폰 타입은 필수입니다.")
    private DiscountType discountType;

    @NotNull(message = "할인율은 필수입니다.")
    @Min(value = 1)
    private Integer discountValue;

    private Integer totalQuantity;

    private Integer maxDiscountAmount;

    private Integer minOrderAmount;

    private LocalDateTime issueStartAt;

    @Future(message = "발급 종료일은 현재 시간 이후여야 합니다")
    private LocalDateTime issueEndAt;

    @NotNull(message = "유효 기간은 필수입니다.")
    @Min(value = 1, message = "유효 기간은 최소 1일 이상이여야 합니다.")
    private Integer validityDays;

    public static CreateCouponCommand of (CreateCouponReq request) {
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
                .validityDays(request.getValidityDays())
                .build();
    }
}
