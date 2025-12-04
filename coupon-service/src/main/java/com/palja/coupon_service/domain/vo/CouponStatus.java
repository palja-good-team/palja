package com.palja.coupon_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponStatus {
    ACTIVE("활성") {
        @Override
        public boolean canTransitionTo(CouponStatus newStatus) {
            return newStatus == PAUSED || newStatus == EXPIRED || newStatus == DELETED;
        }
    },
    PAUSED("일시정지") {
        @Override
        public boolean canTransitionTo(CouponStatus newStatus) {
            return newStatus == ACTIVE || newStatus == EXPIRED || newStatus == DELETED;
        }
    },
    EXPIRED("만료") {
        @Override
        public boolean canTransitionTo(CouponStatus newStatus) {
            return newStatus == DELETED;
        }
    },
    DELETED("삭제") {
        @Override
        public boolean canTransitionTo(CouponStatus newStatus) {
            return false;
        }
    },

    ;

    private final String description;

    // 특정 상태로 전환 가능한지 확인
    public abstract boolean canTransitionTo(CouponStatus newStatus);
}
