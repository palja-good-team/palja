package com.palja.payment_service.domain.entity;

import com.palja.common.entity.BaseEntity;
import com.palja.payment_service.application.dto.response.PGPaymentRes;
import com.palja.payment_service.domain.vo.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
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

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

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
    public PaymentLog(Payment payment, UUID orderId, Long userId, BigDecimal amount,
                      PaymentStatus status, String paymentKey,
                      String pgResponseCode, String pgResponseMessage,
                      LocalDateTime processedAt) {
        this.payment = payment;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.paymentKey = paymentKey;
        this.pgResponseCode = pgResponseCode;
        this.pgResponseMessage = pgResponseMessage;
        this.processedAt = processedAt;
    }

    public static PaymentLog createPendingLog(Payment payment) {
        return PaymentLog.builder()
                .payment(payment)
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(PaymentStatus.PENDING)
                .paymentKey(Objects.toString(payment.getPaymentKey(), ""))
                .pgResponseCode("PENDING")
                .pgResponseMessage("결제 생성(PENDING), 결제 완료 X")
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentLog createApprovedLog(Payment payment, PGPaymentRes pgRes) {
        return PaymentLog.builder()
                .payment(payment)
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(PaymentStatus.APPROVED)
                .paymentKey(resolvePaymentKey(pgRes, payment))
                .pgResponseCode(pgRes.getPgResponseCode())
                .pgResponseMessage(pgRes.getPgResponseMessage())
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentLog createFailedLog(Payment payment, PGPaymentRes pgRes) {
        return PaymentLog.builder()
                .payment(payment)
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(PaymentStatus.FAILED)
                .paymentKey(resolvePaymentKey(pgRes, payment))
                .pgResponseCode(pgRes.getPgResponseCode())
                .pgResponseMessage(pgRes.getPgResponseMessage())
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentLog createCanceledLog(Payment payment, PGPaymentRes pgRes) {
        return PaymentLog.builder()
                .payment(payment)
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(PaymentStatus.CANCELED)
                .paymentKey(resolvePaymentKey(pgRes, payment))
                .pgResponseCode(pgRes.getPgResponseCode())
                .pgResponseMessage(pgRes.getPgResponseMessage())
                .processedAt(LocalDateTime.now())
                .build();
    }

    public static PaymentLog createCancelFailedLog(Payment payment, PGPaymentRes pgRes) {
        return PaymentLog.builder()
                .payment(payment)
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(PaymentStatus.APPROVED)
                .paymentKey(resolvePaymentKey(pgRes, payment))
                .pgResponseCode(pgRes != null ? pgRes.getPgResponseCode() : null)
                .pgResponseMessage("[CANCEL_FAILED] " + (pgRes != null ? pgRes.getPgResponseMessage() : "unknown"))
                .processedAt(LocalDateTime.now())
                .build();
    }

    private static String resolvePaymentKey(PGPaymentRes pgRes, Payment payment) {
        return Objects.toString(
                pgRes != null ? pgRes.getPaymentKey() : null,
                Objects.toString(payment.getPaymentKey(), "")
        );
    }
}
