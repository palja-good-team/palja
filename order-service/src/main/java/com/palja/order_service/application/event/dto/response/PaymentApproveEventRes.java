package com.palja.order_service.application.event.dto.response;

import com.palja.order_service.application.command.CompleteOrderPaymentCommand;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentApproveEventRes {

    private UUID paymentId;
    private UUID orderId;
    private Long userId;

    private BigDecimal paidAmount;
    private String paymentKey;

    private String pgResponseCode;
    private String pgResponseMessage;

    private LocalDateTime approvedAt;

    /**
     * 결제 승인 이벤트 → 주문 결제 완료 Command 변환
     */
    public CompleteOrderPaymentCommand toCommand() {
        return CompleteOrderPaymentCommand.builder()
                .orderId(orderId)
                .paymentId(paymentId)
                .paidAmount(paidAmount)
                .build();
    }
}