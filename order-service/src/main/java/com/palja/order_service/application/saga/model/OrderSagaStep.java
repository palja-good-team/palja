package com.palja.order_service.application.saga.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Saga Step 진행 단계
 *
 * [순서]
 * STARTED(0) → STOCK_RESERVED(10) → COUPON_APPLIED(20) → PAYMENT_CREATED(30) → COMPLETED(90)
 *
 *  [code 사용]
 * - 명시적인 순서 관리
 */
@Getter
@RequiredArgsConstructor
public enum OrderSagaStep {

    STARTED(0) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return next == STOCK_RESERVED || next == FAILED;
        }

        @Override
        public boolean isTerminal() {
            return false;
        }

        @Override
        public boolean isExecutable() {
            return false;
        }
    },

    STOCK_RESERVED(10) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            // 쿠폰 없으면 바로 결제로 갈 수 있음
            return next == COUPON_APPLIED || next == PAYMENT_CREATED || next == FAILED;
        }

        @Override
        public boolean isTerminal() {
            return false;
        }

        @Override
        public boolean isExecutable() {
            return true;
        }
    },

    COUPON_APPLIED(20) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return next == PAYMENT_CREATED || next == FAILED;
        }

        @Override
        public boolean isTerminal() {
            return false;
        }

        @Override
        public boolean isExecutable() {
            return true;
        }
    },

    PAYMENT_CREATED(30) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return next == COMPLETED || next == FAILED;
        }

        @Override
        public boolean isTerminal() {
            return false;
        }

        @Override
        public boolean isExecutable() {
            return true;
        }
    },

    COMPLETED(100) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return false; // 종료 상태에서는 전이 불가
        }

        @Override
        public boolean isTerminal() {
            return true;
        }

        @Override
        public boolean isExecutable() {
            return false;
        }
    },

    FAILED(999) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return false; // 종료 상태에서는 전이 불가
        }

        @Override
        public boolean isTerminal() {
            return true;
        }

        @Override
        public boolean isExecutable() {
            return false;
        }
    };

    /**
     * Saga 단계 비교용 코드
     * - ordinal() 대신 사용
     * - 중간 단계 추가/순서 변경에도 안전
     * - 10 단위로 증가 (중간 단계 추가 여유)
     */
    private final int code;

    /**
     * 상태 전이 가능 여부
     */
    public abstract boolean canTransitionTo(OrderSagaStep next);

    /**
     * 종료 상태 여부
     */
    public abstract boolean isTerminal();

    /**
     * 실행 가능한 Step인지
     */
    public abstract boolean isExecutable();

    /**
     * 실행 가능한 Step들만 code 순서대로 반환
     */
    public static List<OrderSagaStep> getExecutableSteps() {
        return Arrays.stream(values())
                .filter(OrderSagaStep::isExecutable)
                .sorted((a, b) -> Integer.compare(a.code, b.code))
                .collect(Collectors.toList());
    }

    /**
     * 특정 Step에 도달했는지
     */
    public boolean isAtLeast(OrderSagaStep step) {
        return this.code >= step.code;
    }
}
