package com.palja.coupon_service.application.dto.couponUser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.palja.coupon_service.domain.entity.Coupon;
import com.palja.coupon_service.domain.entity.CouponUser;
import com.palja.coupon_service.domain.vo.CouponUserStatus;
import com.palja.coupon_service.domain.vo.DiscountType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReadCouponUserDetailRes {
    private UUID couponUserId;
    private UUID couponId;
    private String userId;
    private String couponName;
    private String couponDescription;
    private DiscountType discountType;
    private Integer discountValue;
    private Integer maxDiscountAmount;
    private Integer minOrderAmount;
    private LocalDateTime expireAt;
    private CouponUserStatus status;
    private LocalDateTime usedAt;
    private UUID orderId;
    private Long discountAmount;

    public static ReadCouponUserDetailRes from(CouponUser couponUser) {
        Coupon coupon = couponUser.getCoupon();

        return ReadCouponUserDetailRes.builder()
                .couponUserId(couponUser.getId())
                .couponId(coupon.getId())
                .userId(couponUser.getUserId())
                .couponName(coupon.getName())
                .couponDescription(coupon.getDescription())
                .discountType(coupon.getDiscountPolicy().getDiscountType())
                .discountValue(coupon.getDiscountPolicy().getDiscountValue())
                .maxDiscountAmount(coupon.getAmountPolicy().getMaxDiscountAmount())
                .minOrderAmount(coupon.getAmountPolicy().getMinOrderAmount())
                .expireAt(couponUser.getExpireAt())
                .status(couponUser.getStatus())
                .usedAt(couponUser.getUsedAt())
                .orderId(couponUser.getOrderId())
                .discountAmount(couponUser.getDiscountAmount())
                .build();
    }
}
