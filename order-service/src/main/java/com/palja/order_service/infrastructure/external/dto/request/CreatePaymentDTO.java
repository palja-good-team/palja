package com.palja.order_service.infrastructure.external.dto.request;

import com.palja.order_service.domain.vo.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentDTO {
    private UUID orderId;
    private Long userId;
    private BigDecimal amount;
    private OrderStatus orderStatus;
}