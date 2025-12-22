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
        public boolean isOrderCancellable() {
            return true;
        }
        @Override
        public boolean isCreated() {
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
        public boolean isOrderCancellable() {
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
        public boolean isOrderCancellable() {
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

    // ==== 헬퍼 메서드 (필요한 것만 override) ===== //

    /** 최종 상태 여부 (CANCELED, COMPLETED) */
    public boolean isFinalState() {
        return false;
    }

    /** 주문 생성 상태인지 */
    public boolean isCreated() {
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
     * 상태 기준 취소 가능 상태인지
     * - CREATED / PAID / PREPARING
     * - 실제 취소 가능 여부는 배송 상태(READY/REQUESTED 이하)까지 같이 확인
     */
    public boolean isOrderCancellable() {
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

    /**
     * 송장 등록 가능 여부
     * - 결제 완료(PAID) 이후 물류 처리가 시작될 때 송장 등록
     */
    public boolean canRegisterTracking() {
        return this == PAID;
    }

    // ======= 관리자 권한 검증 =======
    /**
     * 관리자 권한으로 변경 가능한 상태인지 검증
     * - 최종 상태(CANCELED, COMPLETED)로의 전환 불가
     *    - CANCELED: 취소 API 사용 필요 (환불, 재고 복구 등 보상 트랜잭션)
     *    - COMPLETED: 확정 API 사용 필요 (정산 처리)
     * - 최종 상태에서 다른 상태로 전환 불가 (되돌릴 수 없음)
     *    - CANCELED → 이미 환불/재고복구 완료
     *    - COMPLETED → 이미 정산 완료
     */
    public void validateManagerTransition(OrderStatus targetStatus) {
        // 최종 상태에서의 전환 차단
        if (this.isFinalState()) {
            throw new IllegalStateException(
                    String.format("%s(%s) 상태에서는 다른 상태로 변경할 수 없습니다. " +
                                    "이미 처리가 완료된 주문입니다.",
                            this.name(), this.description)
            );
        }

        // 최종 상태(CANCELED, COMPLETED)로의 직접 전환 차단
        if (targetStatus.isFinalState()) {
            throw new IllegalStateException(
                    String.format("%s(%s) 상태로는 직접 변경할 수 없습니다. " +
                                    "해당 상태 전용 API를 사용해주세요.",
                            targetStatus.name(), targetStatus.description)
            );
        }
    }

    public static OrderStatus from(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null; // 잘못된 값이면 필터 미적용
        }
    }
}