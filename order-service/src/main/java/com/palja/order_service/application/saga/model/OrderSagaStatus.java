package com.palja.order_service.application.saga.model;

// DB 조회용
public enum OrderSagaStatus {
    STARTED,       // 시작됨
    COMPENSATING,  // 보상 중
    COMPLETED,     // 완료됨
    FAILED         // 실패함
}