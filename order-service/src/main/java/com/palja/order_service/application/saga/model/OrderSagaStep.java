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
 * STARTED(0) → STOCK_DECREASED(10) → COUPON_USED(20) → PAYMENT_CREATED(30) → COMPLETED(100)
 *
 *  [code 사용]
 * - 명시적인 순서 관리
 * - 10 단위 증가로 중간 단계 추가 가능
 */
@Getter
@RequiredArgsConstructor
public enum OrderSagaStep {

    STARTED(0) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            return next == STOCK_DECREASED || next == FAILED;
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

    STOCK_DECREASED(10) {
        @Override
        public boolean canTransitionTo(OrderSagaStep next) {
            // 쿠폰 없으면 결제로 스킵 가능
            return next == COUPON_USED || next == PAYMENT_CREATED || next == FAILED;
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

    COUPON_USED(20) {
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
     */
    private final int code;

    /**
     * 다음 단계로 전이 가능한지 검증
     */
    public abstract boolean canTransitionTo(OrderSagaStep next);

    /**
     * 종료 상태인지 확인 (COMPLETED, FAILED)
     */
    public abstract boolean isTerminal();

    /**
     * 실행 가능한 Step인지 확인
     */
    public abstract boolean isExecutable();

    /**
     * 실행 가능한 Step 목록을 순서대로 반환 (code 순서)
     */
    public static List<OrderSagaStep> getExecutableSteps() {
        return Arrays.stream(values())
                .filter(OrderSagaStep::isExecutable)
                .sorted((a, b) -> Integer.compare(a.code, b.code))
                .collect(Collectors.toList());
    }

    /**
     * 특정 Step 이상인지 확인
     */
    public boolean isAtLeast(OrderSagaStep step) {
        return this.code >= step.code;
    }
}
