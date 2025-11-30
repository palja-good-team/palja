package com.palja.payment_service.domain.entity;

import com.palja.common.entity.BaseEntity;
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
@Table(name = "p_payment_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_log_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "payment_key", nullable = false)
    private String paymentKey;

    @Column(name = "pg_response_code")
    private String pgResponseCode;

    @Column(name = "pg_response_message")
    private String pgResponseMessage;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @Builder
    public PaymentLog(Payment payment, Long userId, BigDecimal amount,
                      PaymentStatus status, String paymentKey,
                      String pgResponseCode, String pgResponseMessage,
                      LocalDateTime processedAt) {
        this.payment = payment;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.paymentKey = paymentKey;
        this.pgResponseCode = pgResponseCode;
        this.pgResponseMessage = pgResponseMessage;
        this.processedAt = processedAt;
    }
}
