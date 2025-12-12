package com.palja.payment_service.presentation.dto.request;

import com.palja.payment_service.application.command.CompletePaymentCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class CompletePaymentReq {

    @NotBlank(message = "paymentKey는 필수입니다.")
    private String paymentKey;

    public CompletePaymentCommand toCommand(UUID paymentId, String loginIdFromContext) {
        return CompletePaymentCommand.builder()
                .paymentId(paymentId)
                .paymentKey(paymentKey)
                .loginId(loginIdFromContext)
                .build();
    }
}
