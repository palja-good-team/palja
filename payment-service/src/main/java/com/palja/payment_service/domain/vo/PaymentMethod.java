package com.palja.payment_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CARD("카드"),
    ACCOUNT("계좌이체"),
    EASY_PAY("간편결제"),
    MOBILE("휴대폰 결제"),
    VIRTUAL_ACCOUNT("가상계좌");

    private final String description;
}

