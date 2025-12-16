package com.palja.order_service.application.saga.model;

// DB 조회용
public enum OrderSagaStatus {
    PROCESSING,
    COMPLETED,
    FAILED
}