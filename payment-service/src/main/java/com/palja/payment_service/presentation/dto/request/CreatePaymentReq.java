package com.palja.payment_service.presentation.dto.request;

import com.palja.payment_service.application.command.CreatePaymentCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CreatePaymentReq {

    private UUID orderId;
    private BigDecimal amount;
    private String paymentMethod;
    private String currency;
    private String paymentKey;

    public CreatePaymentCommand toCommand(String loginId) {
        return CreatePaymentCommand.builder()
                .orderId(orderId)
                .loginId(loginId)
                .amount(amount)
                .currency(currency)
                .paymentMethod(paymentMethod)
                .paymentKey(paymentKey)
                .build();
    }
}
