package com.palja.order_service.infrastructure.external.dto.request;

import com.palja.order_service.application.dto.PaymentMethod;
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
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private String currency;
    private String paymentKey;
}