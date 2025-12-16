package com.palja.order_service.infrastructure.saga.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_order_saga")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class OrderSaga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_saga_id", nullable = false, updatable = false)
    private UUID sagaId;

    @Column(name = "order_id", nullable = false, updatable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status", nullable = false)
    private OrderSagaStatus sagaStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_step", nullable = false)
    private OrderSagaStep sagaStep;

    @Column(name = "saga_fail_reason", length = 255)
    private String sagaFailReason;

    @Version
    @Column(name = "version")
    private Long version;

    public static OrderSaga create(UUID orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("주문 ID는 필수입니다.");
        }

        return OrderSaga.builder()
                .orderId(orderId)
                .sagaStatus(OrderSagaStatus.PROCESSING)
                .sagaStep(OrderSagaStep.STARTED)
                .build();
    }

    /**
     * Saga Step 전이 (전이 규칙 강제)
     */
    public void markStep(OrderSagaStep nextStep) {
        validateTransition(nextStep);
        this.sagaStep = nextStep;
    }

    /**
     * Saga 완료 처리
     */
    public void complete() {
        ensureNotTerminal();

        this.sagaStatus = OrderSagaStatus.COMPLETED;
        this.sagaStep = OrderSagaStep.COMPLETED;
        this.sagaFailReason = null;
    }

    /**
     * Saga 실패 처리
     */
    public void fail(String reason) {
        ensureNotTerminal();

        this.sagaStatus = OrderSagaStatus.FAILED;
        this.sagaStep = OrderSagaStep.FAILED;
        this.sagaFailReason = (reason == null || reason.isBlank())
                ? "Saga 처리 중 오류가 발생했습니다."
                : reason;
    }

    /**
     * Saga 종료 여부
     */
    public boolean isTerminal() {
        return this.sagaStep.isTerminal();
    }

    /**
     * 현재 Step 조회 (Order에서 getSagaStep() 하던 것 대체)
     */
    public OrderSagaStep currentStep() {
        return this.sagaStep;
    }

    /* =========================
     * Validation
     * ========================= */

    private void validateTransition(OrderSagaStep next) {
        if (next == null) {
            throw new IllegalArgumentException("다음 Saga 단계는 필수입니다.");
        }

        ensureNotTerminal();

        // 동일 step 재설정 방지 (중복 호출이면 버그로 보고 막는다)
        if (this.sagaStep == next) {
            throw new IllegalStateException(
                    String.format("Saga 단계가 이미 %s 입니다.", this.sagaStep)
            );
        }

        // 도메인 전이 규칙
        if (!this.sagaStep.canTransitionTo(next)) {
            throw new IllegalStateException(
                    String.format("허용되지 않은 Saga 단계 전이입니다. (%s → %s)", this.sagaStep, next)
            );
        }
    }

    private void ensureNotTerminal() {
        if (isTerminal()) {
            throw new IllegalStateException(
                    String.format("이미 종료된 Saga 입니다. (status=%s, step=%s)", this.sagaStatus, this.sagaStep)
            );
        }
    }
}