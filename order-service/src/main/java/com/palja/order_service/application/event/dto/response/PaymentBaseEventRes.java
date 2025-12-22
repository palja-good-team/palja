package com.palja.order_service.application.event.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentBaseEventRes {

    private String type;
    private UUID eventId;
    private UUID orderId;
    private UUID paymentId;
    private LocalDateTime occurredAt;
    // 내부 JSON 문자열
    private String payloadJson;
}