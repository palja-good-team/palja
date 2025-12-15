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

    public CreatePaymentCommand toCommand(String loginIdFromContext) {
        return CreatePaymentCommand.builder()
                .orderId(orderId)
                .userId(userId)
                .loginId(loginIdFromContext)
                .amount(amount)
                .orderStatus(orderStatus)
                .build();
    }
}
