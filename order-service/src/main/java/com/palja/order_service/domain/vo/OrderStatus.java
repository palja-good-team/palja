package com.palja.order_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
    CREATED("주문 생성"),
    PAID("결제 완료"),
    PREPARING("상품 준비 중"),
    SHIPPED("배송 출발"),
    DELIVERED("배송 완료"),
    COMPLETED("구매 확정"),
    CANCELED("주문 취소");

    private final String description;
}