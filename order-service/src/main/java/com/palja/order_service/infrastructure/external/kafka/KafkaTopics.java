package com.palja.order_service.infrastructure.external.kafka;

/**
 * Kafka Topic 상수 정의
 */
public class KafkaTopics {

    // Order Create Saga (정방향)
    // Saga 시작 요청
    public static final String ORDER_CREATE_SAGA_START = "order.create.saga.start";

    // 재고 차감 요청/응답
    public static final String STOCK_DEDUCT_REQUEST = "order.deduct.request";
    public static final String STOCK_DEDUCT_RESPONSE = "order.deduct.response";

    // 쿠폰 사용 요청/응답
    public static final String COUPON_USE_REQUEST = "order.use.request";
    public static final String COUPON_USE_RESPONSE = "order.use.response";

    // 결제 생성 요청/응답
    public static final String PAYMENT_CREATE_REQUEST = "order.create.request";
    public static final String PAYMENT_CREATE_RESPONSE = "order.create.response";

    // Order Create Saga (보상)
    // 재고 복구 요청/응답
    public static final String STOCK_RESTORE_REQUEST = "order.restore.request";
    public static final String STOCK_RESTORE_RESPONSE = "order.restore.response";
    // 쿠폰 취소 요청/응답
    public static final String COUPON_CANCEL_REQUEST = "order.cancel.request";
    public static final String COUPON_CANCEL_RESPONSE = "order.cancel.response";
    // 결제 취소 요청 요청/응답
    public static final String PAYMENT_CANCEL_REQUEST = "order.cancel.request";
    public static final String PAYMENT_CANCEL_RESPONSE = "order.cancel.response";

    private KafkaTopics() {
        throw new AssertionError("Cannot instantiate");
    }}