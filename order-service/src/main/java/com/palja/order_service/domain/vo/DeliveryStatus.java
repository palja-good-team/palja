package com.palja.order_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {

    READY("배송 준비 (출고 전)") {
        @Override
        public boolean canTransitionTo(DeliveryStatus newStatus) {
            return newStatus == REQUESTED;
        }

        @Override
        public boolean isOrderCancellable() {
            return true;                // 주문 취소 가능
        }

        @Override
        public boolean isBeforeTransit() {
            return true;                // 집하 전
        }

        @Override
        public boolean isDeliveryEditable() {
            return true;                // 배송 정보 수정 가능 (READY만)
        }
    },

    REQUESTED("송장 발행/배송요청 (수거 전)") {
        @Override
        public boolean canTransitionTo(DeliveryStatus newStatus) {
            return newStatus == IN_TRANSIT;
        }

        @Override
        public boolean isOrderCancellable() {
            return true;                // 주문 취소 가능
        }

        @Override
        public boolean isBeforeTransit() {
            return true;                // 집하 전
        }
    },

    IN_TRANSIT("배송 중 (집하/이동 시작)") {
        @Override
        public boolean canTransitionTo(DeliveryStatus newStatus) {
            return newStatus == DELIVERED;
        }

        @Override
        public boolean isInTransit() {
            return true;
        }
    },

    DELIVERED("배송 완료") {
        @Override
        public boolean canTransitionTo(DeliveryStatus newStatus) {
            return false;
        }

        @Override
        public boolean isDelivered() {
            return true;
        }

        @Override
        public boolean isFinalState() {
            return true;
        }
    };

    private final String description;

    // --- 상태 전이 검증 ---

    public abstract boolean canTransitionTo(DeliveryStatus newStatus);

    public void validateTransition(DeliveryStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format(
                            "배송 상태를 %s(%s) → %s(%s) 로 변경할 수 없습니다.",
                            this.name(), this.description,
                            newStatus.name(), newStatus.description
                    )
            );
        }
    }

    // --- 기본 정책 (override하지 않으면 false) ---

    /** 배송 상태 기준 주문 취소 가능 여부 (READY, REQUESTED만 true) */
    public boolean isOrderCancellable() {
        return false;
    }

    /** 배송 시작 전 여부 (READY, REQUESTED) */
    public boolean isBeforeTransit() {
        return false;
    }

    /** 실제 배송 진행 중 여부 */
    public boolean isInTransit() {
        return false;
    }

    /** 배송 완료 여부 */
    public boolean isDelivered() {
        return false;
    }

    /** 최종 상태(더 이상 변경 불가) 여부 */
    public boolean isFinalState() {
        return false;
    }

    /** 배송 정보 수정 가능 여부 (정책: READY만 true) */
    public boolean isDeliveryEditable() {
        return false;
    }
}