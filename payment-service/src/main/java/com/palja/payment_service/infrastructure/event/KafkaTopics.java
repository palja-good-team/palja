package com.palja.payment_service.infrastructure.event;

public final class KafkaTopics {
    private KafkaTopics() {}

    public static final String PAYMENT_ORDER_APPROVE_SUCCESS = "payment.order.approve.success";
    public static final String PAYMENT_ORDER_APPROVE_FAILURE = "payment.order.approve.failure";
    public static final String PAYMENT_ORDER_CANCEL_SUCCESS  = "payment.order.cancel.success";
    public static final String PAYMENT_ORDER_CANCEL_FAILURE  = "payment.order.cancel.failure";
}

