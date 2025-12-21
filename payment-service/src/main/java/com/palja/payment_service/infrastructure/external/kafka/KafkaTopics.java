package com.palja.payment_service.infrastructure.external.kafka;

public final class KafkaTopics {
    private KafkaTopics() {}

    // 발행 (Payment -> Order)
    public static final String PAYMENT_ORDER_APPROVE_SUCCESS = "payment.order.approve.success";
    public static final String PAYMENT_ORDER_APPROVE_FAILURE = "payment.order.approve.failure";
    public static final String PAYMENT_ORDER_CANCEL_SUCCESS  = "payment.order.cancel.success";
    public static final String PAYMENT_ORDER_CANCEL_FAILURE  = "payment.order.cancel.failure";

    // 요청 (Order -> Payment)
    public static final String PAYMENT_CREATE_REQUEST = "order.payment.create.request";
    public static final String PAYMENT_CANCEL_REQUEST = "order.cancel.request";

    // 응답 (Payment -> Order)
    public static final String PAYMENT_CREATE_SUCCESS = "order.payment.create.success";
    public static final String PAYMENT_CREATE_FAILURE = "order.payment.create.failure";
}

