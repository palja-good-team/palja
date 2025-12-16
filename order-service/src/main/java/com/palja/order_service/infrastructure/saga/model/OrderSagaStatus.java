package com.palja.order_service.infrastructure.saga.model;

// DB 조회용
public enum OrderSagaStatus {
    PROCESSING,
    COMPLETED,
    FAILED
}