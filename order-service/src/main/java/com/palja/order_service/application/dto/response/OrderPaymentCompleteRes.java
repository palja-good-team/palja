package com.palja.order_service.application.dto.response;

import com.palja.order_service.domain.entity.Order;
import com.palja.order_service.domain.vo.OrderStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

// 결제 완료 응답
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderPaymentCompleteRes {
        private UUID orderId;
        private UUID paymentId;
        private OrderStatus status;
        private BigDecimal finalAmount;

    public static OrderPaymentCompleteRes from(Order order) {
        return OrderPaymentCompleteRes.builder()
                .orderId(order.getOrderId())
                .paymentId(order.getOrderId())
                .status(order.getStatus())
                .finalAmount(order.getOrderAmount().getFinalAmount())
                .build();
    }
}