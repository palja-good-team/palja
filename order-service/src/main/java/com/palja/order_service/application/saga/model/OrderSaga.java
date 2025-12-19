package com.palja.order_service.application.saga.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * OrderSaga 엔티티
 * - orderId만 참조 (단방향)
 * - Saga의 생명주기를 관리
 */
@Getter
@Entity
@Table(name = "p_order_saga")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class OrderSaga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "saga_id")
    private UUID sagaId;

    @Column(name = "order_id", nullable = false, unique = true, updatable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrderSagaStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_step", nullable = false, length = 30)
    private OrderSagaStep currentStep;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public static OrderSaga create(UUID orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("주문 ID는 필수입니다.");
        }

        return OrderSaga.builder()
                .orderId(orderId)
                .status (OrderSagaStatus.STARTED)
                .currentStep(OrderSagaStep.STARTED)
                .build();
    }

    // 다음 단계로 진행
    public void proceedToNextStep(OrderSagaStep nextStep) {
        validateNotTerminated();
        validateStepTransition(nextStep);

        this.currentStep = nextStep;
    }

    // Saga 완료 처리
    public void complete() {
        validateNotTerminated();

        this.status = OrderSagaStatus.COMPLETED;
        this.currentStep = OrderSagaStep.COMPLETED;
    }

    // Saga 실패 처리
    public void fail(String reason) {
        validateNotTerminated();

        this.status = OrderSagaStatus.FAILED;
        this.currentStep = OrderSagaStep.FAILED;
        this.failureReason = (reason == null || reason.isBlank())
                ? "Saga 처리 중 오류가 발생했습니다."
                : reason;
    }

    // 보상 중 상태로 변경
    public void startCompensation() {
        validateNotTerminated();
        this.status = OrderSagaStatus.COMPENSATING;
    }

    // Saga 종료 여부
    public boolean isTerminated() {
        return currentStep.isTerminal();
    }

    public boolean isInProgress() {
        return status == OrderSagaStatus.STARTED || status == OrderSagaStatus.COMPENSATING;
    }

    public boolean hasReachedStep(OrderSagaStep step) {
        return this.currentStep.isAtLeast(step);
    }

    // ===== Validation =====

    private void validateNotTerminated() {
        if (isTerminated()) {
            throw new IllegalStateException(
                    String.format("이미 종료된 Saga입니다. sagaId=%s, status=%s", sagaId, status)
            );
        }
    }

    private void validateStepTransition(OrderSagaStep nextStep) {
        if (nextStep == null) {
            throw new IllegalArgumentException("다음 Saga 단계는 필수입니다.");
        }

        // 종료된 Saga는 더 이상 진행 불가
        validateNotTerminated();

        // 동일 step이면 멱등 처리 (이미 처리됨 → 정상 종료)
        if (this.currentStep == nextStep) {
            return;
        }

        // 도메인 전이 규칙
        if (!this.currentStep.canTransitionTo(nextStep)) {
            throw new IllegalStateException(
                    String.format("허용되지 않은 Saga 단계 전이입니다. (%s → %s)", this.currentStep, nextStep)
            );
        }
    }
}