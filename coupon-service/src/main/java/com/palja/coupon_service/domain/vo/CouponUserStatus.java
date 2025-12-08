package com.palja.coupon_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponUserStatus {
    ISSUED("발행됨") {
        @Override
        public boolean canTransitionTo(CouponUserStatus newStatus) {
            return newStatus == USED || newStatus == EXPIRED;
        }
    },
    USED("사용완료") {
        @Override
        public boolean canTransitionTo(CouponUserStatus newStatus) {
            return false;
        }
    },
    EXPIRED("만료됨") {
        @Override
        public boolean canTransitionTo(CouponUserStatus newStatus) {
            return false;
        }
    },
    ;

    private final String description;

    // 특정 상태로 전환 가능한지 확인
    public abstract boolean canTransitionTo(CouponUserStatus newStatus);
}
