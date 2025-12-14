package com.palja.order_service.presentation.dto.request;

import com.palja.order_service.application.command.CompleteOrderPaymentCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

// 주문 결제 완료 요청 DTO
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOrderPaymentReq {
    @NotNull(message = "결제 ID는 필수입니다.")
    private UUID paymentId;

    @NotNull(message = "결제 금액은 필수입니다.")
    @DecimalMin(value = "0.01", message = "결제 금액은 0보다 커야 합니다.")
    private BigDecimal paidAmount;

    public CompleteOrderPaymentCommand toCommand(UUID orderId) {
        return CompleteOrderPaymentCommand.builder()
                .orderId(orderId)
                .paymentId(paymentId)
                .paidAmount(paidAmount)
                .build();
    }
}