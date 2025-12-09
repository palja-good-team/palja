package com.palja.order_service.infrastructure.external.dto.response;

import com.palja.order_service.application.dto.response.PaymentCreateRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCreateDTO {

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

    public PaymentCreateRes toResponse() {
        return PaymentCreateRes.builder()
                .paymentId(paymentId)
                .amount(amount)
                .build();
    }

    // TODO: 결제 서비스 연동 완료 전까지 사용하는 더미 데이터 (TOSS_SECRET_KEY 받으면 제거)
    public PaymentCreateRes toResponseByDummy() {
        return PaymentCreateRes.builder()
                .paymentId(UUID.fromString("10000000-0000-0000-1000-000000000000"))
                .amount(BigDecimal.valueOf(30000))
                .build();
    }
}
