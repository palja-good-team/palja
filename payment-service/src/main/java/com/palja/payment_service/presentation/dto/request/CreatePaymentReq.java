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
    private Long userId;
    private BigDecimal amount;
    private String orderStatus;
    private String paymentMethod;
    private String currency;
    private String paymentKey;

    public CreatePaymentCommand toCommand(String loginIdFromContext) {
        String finalPaymentMethod = (paymentMethod != null && !paymentMethod.isBlank())
                ? paymentMethod
                : "CARD";
        String finalCurrency = (currency != null && !currency.isBlank())
                ? currency
                : "KRW";

        return CreatePaymentCommand.builder()
                .orderId(orderId)
                .userId(userId)
                .loginId(loginIdFromContext)
                .amount(amount)
                .currency(finalCurrency)
                .paymentMethod(finalPaymentMethod)
                .paymentKey(paymentKey)
                .orderStatus(orderStatus)
                .build();
    }
}
