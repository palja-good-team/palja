package com.palja.order_service.infrastructure.external.dto.response;

import com.palja.order_service.application.dto.response.PaymentCancelRes;
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

    public PaymentCancelRes toResponse() {
        return PaymentCancelRes.builder()
                .paymentId(paymentId)
                .amount(amount)
                .build();
    }

    // TODO: 결제 서비스 연동 완료 전까지 사용하는 더미 데이터 (TOSS_SECRET_KEY 받으면 제거)
    public PaymentCancelRes toResponseDummy(UUID paymentId) {
        return PaymentCancelRes.builder()
                .paymentId(paymentId)
                .amount(BigDecimal.valueOf(30000))
                .build();
    }
}