package com.palja.order_service.infrastructure.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 결제 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

    private UUID paymentId;
    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;   // CARD, ACCOUNT,  EASY_PAY, MOBILE, VIRTUAL_ACCOUNT
    private String currency;        // KRW
    private String paymentKey;      // tossKey-abc123
    private String status;          // PENDING, APPROVED, FAILED, CANCELED
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    /**
     * TODO: 결제 서비스 연동 전까지 사용하는 더미 데이터. payment-service 연동 후 삭제.
     */
    public static PaymentDTO dummy(UUID orderId, Long userId, BigDecimal amount) {
        return new PaymentDTO(
                UUID.randomUUID(),
                orderId,
                userId,
                amount,
                "CARD",
                "KRW",
                "dummy-payment-key-" + UUID.randomUUID(),
                "APPROVED",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
