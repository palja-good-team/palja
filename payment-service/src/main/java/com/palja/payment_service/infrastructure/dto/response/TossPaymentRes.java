package com.palja.payment_service.infrastructure.dto.response;

import lombok.Getter;
import java.time.OffsetDateTime;

@Getter
public class TossPaymentRes {
    private String paymentKey;
    private String orderId;
    private String status;
    private Integer totalAmount;
    private OffsetDateTime approvedAt;
}
