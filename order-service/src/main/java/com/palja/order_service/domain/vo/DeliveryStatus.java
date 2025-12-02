package com.palja.order_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeliveryStatus {
    PENDING("배송 준비 중"),
    REQUESTED("배송 요청"),
    IN_TRANSIT("배송 중"),
    DELIVERED("배송 완료");

    private final String description;
}