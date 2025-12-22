package com.palja.order_service.application.event.dto.response;

import com.palja.common.auditor.CurrentUser;
import com.palja.order_service.application.command.CancelOrderCommand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 결제 생성 응답 이벤트
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCancelEventRes {

    private UUID paymentId;
    private UUID orderId;
    private Long userId;

    private BigDecimal canceledAmount;
    private String cancelReason;
    private String paymentKey;

    private String pgResponseCode;
    private String pgResponseMessage;

    private LocalDateTime canceledAt;

    /**
     * 결제 취소 이벤트 → 주문 취소 Command 변환
     * - 외부(Payment) 시스템에 의해 발생한 취소
     */
    public CancelOrderCommand toCommand() {
        return CancelOrderCommand.builder()
                .orderId(orderId)
                .CurrentUserLoginId(CurrentUser.getLoginId())   // "PAYMENT" 또는 "SYSTEM"
                .CurrentUserRole(CurrentUser.getRole())         // "SYSTEM"
                .cancelReason(cancelReason != null ? cancelReason : "PAYMENT_CANCELED")
                .build();
    }
}