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

    // 유효성 검증
    public void validate() {
        if (recipientName == null || recipientName.isBlank()) {
            throw new IllegalArgumentException("수령인 이름은 필수입니다.");
        }
        if (recipientAddress == null || recipientAddress.isBlank()) {
            throw new IllegalArgumentException("수령인 주소는 필수입니다.");
        }

        // 길이 검증
        if (recipientName.length() > 50) {
            throw new IllegalArgumentException("수령인 이름은 50자를 초과할 수 없습니다.");
        }
        if (recipientEmail != null && recipientEmail.length() > 255) {
            throw new IllegalArgumentException("이메일은 255자를 초과할 수 없습니다.");
        }
        if (recipientAddress.length() > 200) {
            throw new IllegalArgumentException("수령인 주소는 200자를 초과할 수 없습니다.");
        }
        if (deliveryMessage != null && deliveryMessage.length() > 255) {
            throw new IllegalArgumentException("배송 메시지는 255자를 초과할 수 없습니다.");
        }
    }
}