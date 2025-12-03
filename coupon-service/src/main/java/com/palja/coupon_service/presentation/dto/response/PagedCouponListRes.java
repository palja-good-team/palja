package com.palja.coupon_service.presentation.dto.response;

import com.palja.coupon_service.application.dto.CouponRes;
import com.palja.coupon_service.domain.vo.CouponStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedCouponListRes {
    private UUID couponId;
    private String couponName;
    private DiscountType discountType;
    private int discountValue;
    private LocalDateTime issueStartAt;
    private LocalDateTime issueEndAt;
    private CouponStatus status;

    public static PagedCouponListRes from(CouponRes couponRes) {
        return PagedCouponListRes.builder()
                .couponId(couponRes.getCouponId())
                .couponName(couponRes.getCouponName())
                .discountType(couponRes.getDiscountType())
                .discountValue(couponRes.getDiscountValue())
                .issueStartAt(couponRes.getIssueStartAt())
                .issueEndAt(couponRes.getIssueEndAt())
                .status(couponRes.getStatus())
                .build();
    }
}
