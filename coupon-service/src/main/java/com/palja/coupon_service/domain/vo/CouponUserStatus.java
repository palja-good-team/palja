package com.palja.coupon_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponUserStatus {
    ISSUED("발행됨"),
    USED("사용완료"),
    EXPIRED("만료됨"),
    ;

    private final String description;
}
