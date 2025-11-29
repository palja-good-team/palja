package com.palja.payment_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
    PENDING("결제 요청 대기"),
    APPROVED("결제 승인 완료"),
    FAILED("결제 실패"),
    CANCELED("결제 취소");

    private final String description;
}
