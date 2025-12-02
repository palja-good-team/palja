package com.palja.payment_service.presentation.dto.request;

import com.palja.payment_service.application.command.CreatePaymentCommand;
import com.palja.payment_service.domain.vo.PaymentMethod;
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
    private String paymentMethod;
    private String currency;
    private String paymentKey;

    public CreatePaymentCommand toCommand() {
        return new CreatePaymentCommand(
                orderId,
                userId,
                amount,
                currency,
                PaymentMethod.valueOf(paymentMethod),
                paymentKey
        );
    }
}
