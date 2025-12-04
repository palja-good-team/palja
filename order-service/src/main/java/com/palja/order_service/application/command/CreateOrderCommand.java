package com.palja.order_service.application.command;

import lombok.Builder;

import java.util.UUID;

// 주문 생성 Command
// Presentation Layer에서 전달받은 요청을 Application Layer로 전달
@Builder
public record CreateOrderCommand(
        String loginId,
        UUID productId,
        int quantity,
        UUID timeDealId,
        UUID couponId,
        DeliveryCommand delivery
) {

    // 유효성 검증
    public void validate() {
        if (loginId == null) {
            throw new IllegalArgumentException("사용자 로그인 ID는 필수입니다.");
        }
        if (productId == null) {
            throw new IllegalArgumentException("상품 ID는 필수입니다.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("주문 수량은 1 이상이어야 합니다.");
        }
        if (delivery == null) {
            throw new IllegalArgumentException("배송 정보는 필수입니다.");
        }

        // 배송 정보 검증
        delivery.validate();
    }

    // 타임딜 주문 여부
    public boolean isTimeDealOrder() {
        return timeDealId != null;
    }

    // 쿠폰 사용 여부
    public boolean hasCoupon() {
        return couponId != null;
    }
}