package com.palja.payment_service.infrastructure.saga.topics;

public final class OrderSagaKafkaTopics {
    private OrderSagaKafkaTopics() {}

    // 요청 (Order -> Payment)
    public static final String PAYMENT_CREATE_REQUEST = "order.payment.create.request";
    public static final String PAYMENT_CANCEL_REQUEST = "order.payment.cancel.request";

    // 응답 (Payment -> Order)
    public static final String PAYMENT_CREATE_SUCCESS = "order.payment.create.success";
    public static final String PAYMENT_CREATE_FAILURE = "order.payment.create.failure";
}
