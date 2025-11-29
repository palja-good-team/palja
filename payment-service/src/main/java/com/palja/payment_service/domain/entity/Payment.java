package com.palja.payment_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.payment_service.domain.vo.PaymentMethod;
import com.palja.payment_service.domain.vo.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "payment_key")
    private String paymentKey;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public Payment(UUID orderId, Long userId, BigDecimal amount, String currency, PaymentMethod paymentMethod) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.status = PaymentStatus.PENDING;
        this.requestedAt = LocalDateTime.now();
    }
}
