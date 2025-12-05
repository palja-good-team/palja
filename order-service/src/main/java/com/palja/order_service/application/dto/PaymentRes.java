package com.palja.order_service.application.dto;

import com.palja.order_service.infrastructure.external.dto.response.PaymentDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentRes {

    private final UUID paymentId;
    private final BigDecimal amount;


    // Infrastructure DTO → Application DTO 변환
    public static PaymentRes from(PaymentDTO paymentDTO) {
        return PaymentRes.builder()
                .paymentId(paymentDTO.getPaymentId())
                .amount(paymentDTO.getAmount())
                .build();
    }
}