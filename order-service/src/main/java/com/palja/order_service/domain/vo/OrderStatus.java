package com.palja.order_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    CREATED("주문 생성") {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == PAID || newStatus == CANCELED;
        }

        @Override
        public boolean isCancelableCandidate() {
            return true;
        }
    },

    PAID("결제 완료") {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == PREPARING || newStatus == CANCELED;
        }

        @Override
        public boolean isPaid() {
            return true;
        }

        @Override
        public boolean isCancelableCandidate() {
            return true;
        }
    },

    PREPARING("상품 준비 중") {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == SHIPPED || newStatus == CANCELED;
        }

        @Override
        public boolean isPreparing() {
            return true;
        }

        @Override
        public boolean isCancelableCandidate() {
            return true;
        }
    },

    SHIPPED("배송 출발") {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == DELIVERED;
        }

        @Override
        public boolean isShipped() {
            return true;
        }
    },

    DELIVERED("모든 배송 완료") {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            return newStatus == COMPLETED;
        }

        @Override
        public boolean isDelivered() {
            return true;
        }
    },

    COMPLETED("구매 확정") {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            // 구매확정 이후에는 상태 전이 불가
            return false;
        }

        @Override
        public boolean isCompleted() {
            return true;
        }

        @Override
        public boolean isFinalState() {
            return true;
        }
    },

    CANCELED("주문 취소") {
        @Override
        public boolean canTransitionTo(OrderStatus newStatus) {
            // 취소 이후에는 상태 전이 불가
            return false;
        }

        @Override
        public boolean isCanceled() {
            return true;
        }

        @Override
        public boolean isFinalState() {
            return true;
        }
    };

    private final String description;

    /** 상태 전환 가능 여부 */
    public abstract boolean canTransitionTo(OrderStatus newStatus);

    /** 공통 상태 전환 검증 */
    public void validateTransition(OrderStatus newStatus) {
        if (!canTransitionTo(newStatus)) {
            throw new IllegalStateException(
                    String.format(
                            "주문 상태를 %s(%s) → %s(%s) 로 변경할 수 없습니다.",
                            this.name(), this.description,
                            newStatus.name(), newStatus.description
                    )
            );
        }
    }

    // === 헬퍼 메서드들 (필요한 것만 override) === //

    /** 최종 상태 여부 (CANCELED, COMPLETED 등) */
    public boolean isFinalState() {
        return false;
    }

    /** 결제 완료 상태인지 */
    public boolean isPaid() {
        return false;
    }

    /** 준비중 상태인지 */
    public boolean isPreparing() {
        return false;
    }

    /** 배송 출발 이후인지 (SHIPPED 이상) */
    public boolean isShippedOrBeyond() {
        return this == SHIPPED || this == DELIVERED || this == COMPLETED;
    }

    public boolean isShipped() {
        return false;
    }

    public boolean isDelivered() {
        return false;
    }

    public boolean isCompleted() {
        return false;
    }

    public boolean isCanceled() {
        return false;
    }

    /**
     * "상태 기준"으로만 봤을 때 취소 후보 상태인지
     * - CREATED / PAID / PREPARING
     * - 실제 취소 가능 여부는 배송 상태(READY/REQUESTED 이하)까지 같이 체크해야 함
     */
    public boolean isCancelableCandidate() {
        return false;
    }

    /**
     * 취소 가능한 상태인지 확인
     */
    public boolean isCancellable() {
        return this == CREATED || this == PAID || this == PREPARING;
    }

    /**
     * 구매 확정 가능한 상태인지 확인
     */
    public boolean isConfirmable() {
        return this == DELIVERED;
    }
}