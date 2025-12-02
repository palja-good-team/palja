package com.palja.payment_service.presentation.dto.request;

import com.palja.payment_service.application.command.CancelPaymentCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class CancelPaymentReq {

    private Long userId;
    private BigDecimal cancelAmount;
    private String cancelReason;

    public CancelPaymentCommand toCommand(UUID paymentId) {
        return CancelPaymentCommand.builder()
                .paymentId(paymentId)
                .userId(userId)
                .cancelAmount(cancelAmount)
                .cancelReason(cancelReason)
                .build();
    }
}
