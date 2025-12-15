package com.palja.order_service.domain.vo;

import lombok.Getter;

/**
 * Saga Step 진행 단계
 *
 * [순서]
 * STARTED → INVENTORY_RESERVED → COUPON_APPLIED → PAYMENT_CREATED → COMPLETED
 *
 * [특징]
 * - code 순서가 진행 순서와 일치
 * - isAtLeast()로 완료 여부 판단
 * - canTransitionTo()로 전이 규칙 강제
 */
@Getter
public enum OrderSagaStep {

    STARTED(0) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return next == INVENTORY_RESERVED || next == FAILED;
        }
    },

    INVENTORY_RESERVED(10) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return next == COUPON_APPLIED
                    || next == PAYMENT_CREATED
                    || next == FAILED;
        }
    },

    COUPON_APPLIED(20) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return next == PAYMENT_CREATED
                    || next == FAILED;
        }
    },

    PAYMENT_CREATED(30) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return next == COMPLETED
                    || next == FAILED;
        }
    },

    COMPLETED(90),
    FAILED(99);

    /**
     * Saga 단계 비교용 코드
     * - ordinal() 대신 사용
     * - 중간 단계 추가/순서 변경에도 안전
     */
    private final int code;

    OrderSagaStep(int code) {
        this.code = code;
    }

    /**
     * 다음 단계로 전이 가능 여부
     */
    public boolean canTransitionTo(OrderSagaStep next) {
        return false;
    }

    /**
     * 현재 단계가 target 단계 이상인지 판단
     */
    public boolean isAtLeast(OrderSagaStep target) {
        return this.code >= target.code;
    }

    /**
     * 종료 상태인지 확인
     * @return COMPLETED 또는 FAILED
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED;
    }

    /**
     * 진행 중인지 확인
     * @return COMPLETED, FAILED가 아닌 경우
     */
    public boolean isInProgress() {
        return !isTerminal();
    }

    /**
     * 성공 완료 상태인지 확인
     * @return COMPLETED
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }

    /**
     * 실패 상태인지 확인
     * @return FAILED
     */
    public boolean isFailed() {
        return this == FAILED;
    }
}
