package com.palja.order_service.application.command;


import lombok.Builder;

// 배송 정보 Command
// 주문 생성 시 배송 정보 전달
@Builder
public record DeliveryCommand(
        String recipientName,
        String recipientEmail,
        String recipientAddress,
        String deliveryMessage
) {
}