package com.palja.coupon_service.presentation.dto.response;

import com.palja.coupon_service.application.dto.CouponDTO;
import com.palja.coupon_service.domain.vo.CouponStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouponRes {
    private UUID couponId;
    private String couponName;
    private DiscountType discountType;
    private int discountValue;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private CouponStatus status;

    public static CreateCouponRes from(CouponDTO couponDTO) {
        return CreateCouponRes.builder()
                .couponId(couponDTO.getCouponId())
                .couponName(couponDTO.getCouponName())
                .discountType(couponDTO.getDiscountType())
                .discountValue(couponDTO.getDiscountValue())
                .issueStartAt(couponDTO.getIssueStartAt())
                .issueEndAt(couponDTO.getIssueEndAt())
                .status(couponDTO.getStatus())
                .build();
    }
}
