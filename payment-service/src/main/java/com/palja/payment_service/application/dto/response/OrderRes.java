package com.palja.payment_service.application.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderRes {
    private final UUID orderId;
    private Long userId;
    private final String status;
    private final BigDecimal finalAmount;

    public static OrderRes of(
            UUID orderId,
            Long userId,
            String status,
            BigDecimal finalAmount
    ){
        return OrderRes.builder()
                .orderId(orderId)
                .userId(userId)
                .status(status)
                .finalAmount(finalAmount)
                .build();
    }
}
