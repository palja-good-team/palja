package com.palja.payment_service.infrastructure.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private UUID orderId;
    private Long userId;
    private String status;
    private BigDecimal finalAmount;

    /*
     TODO: 주문 서비스 연동 전까지 사용하는 더미 데이터
           order-service 연결 후 삭제 예정
     */
    public static OrderDTO dummy(UUID orderId) {
        return new OrderDTO(
                orderId,
                1L,
                "CREATED",
                BigDecimal.valueOf(10000)
        );
    }
}
