package com.palja.order_service.infrastructure.external.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentDTO {

    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private String paymentMethod; // CARD 등

    /**
     * TODO: 결제 서비스 연동 전까지 사용하는 더미 데이터. payment-service 연동 후 삭제.
     */
    public static CreatePaymentDTO dummy(UUID orderId, Long userId, BigDecimal amount) {
        return new CreatePaymentDTO(
                orderId,
                userId,
                amount,
                "CARD"
        );
    }
}