package com.palja.order_service.infrastructure.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelDTO {

    private UUID paymentId;
    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod;
    private String currency;
    private String paymentKey;
    private String status;
    private LocalDateTime completedAt;

    // TODO: 결제 서비스 연동 전까지 사용하는 더미 데이터. payment-service 연동 후 삭제.
    public static PaymentCancelDTO dummy(UUID orderId, UUID paymentId) {
        return new PaymentCancelDTO(
                paymentId,
                orderId,
                1L,
                BigDecimal.valueOf(12000),
                "CARD",
                "KRW",
                "tossKey-abc123",
                "CANCELED",
                LocalDateTime.now()
        );
    }
}