package com.palja.payment_service.domain.entity;

import com.palja.common.vo.UserRole;
import com.palja.payment_service.domain.event.vo.OutboxStatus;
import com.palja.payment_service.domain.event.vo.PaymentEventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "p_payment_outbox",
        indexes = {
                @Index(name = "idx_outbox_status", columnList = "status, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentOutbox {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 100)
    private PaymentEventType eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String payload;

    @Column(name = "login_id", length = 255)
    private String loginId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", length = 50)
    private UserRole userRole;

    @Column(name = "trace_id", length = 64)
    private String traceId;

    @Column(name = "span_id", length = 64)
    private String spanId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Builder
    private PaymentOutbox(
            UUID eventId,
            UUID aggregateId,
            UUID orderId,
            PaymentEventType eventType,
            String payload,
            String loginId,
            UserRole userRole,
            String traceId,
            String spanId
    ) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.orderId = orderId;
        this.eventType = eventType;
        this.payload = payload;

        this.loginId = loginId;
        this.userRole = userRole;
        this.traceId = traceId;
        this.spanId = spanId;

        this.status = OutboxStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.retryCount = 0;
    }

    public static PaymentOutbox pending(
            UUID eventId,
            UUID paymentId,
            UUID orderId,
            PaymentEventType type,
            String envelopeJson,
            String loginId,
            UserRole userRole,
            String traceId,
            String spanId
    ) {
        return PaymentOutbox.builder()
                .eventId(eventId)
                .aggregateId(paymentId)
                .orderId(orderId)
                .eventType(type)
                .payload(envelopeJson)
                .loginId(loginId)
                .userRole(userRole)
                .traceId(traceId)
                .spanId(spanId)
                .build();
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    public void markFailed(String errorMessage) {
        this.status = OutboxStatus.FAILED;
        this.errorMessage = errorMessage;
        this.retryCount++;
    }
}
