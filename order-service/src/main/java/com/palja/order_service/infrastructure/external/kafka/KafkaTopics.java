package com.palja.order_service.infrastructure.external.kafka;

/**
 * Kafka Topic 상수 정의
 */
public class KafkaTopics {

    // ====== Request Topics ======
    public static final String SAGA_START_REQUEST = "order.saga.start.request";
    public static final String STOCK_DECREASE_REQUEST = "order.stock.decrease.request";
    public static final String STOCK_RESTORE_REQUEST = "order.stock.restore.request";
    public static final String COUPON_USE_REQUEST = "order.coupon.use.request";
    public static final String COUPON_CANCEL_REQUEST = "order.coupon.cancel.request";
    public static final String PAYMENT_CREATE_REQUEST = "order.payment.create.request";
    public static final String PAYMENT_CANCEL_REQUEST = "order.payment.cancel.request";
    public static final String ORDER_CANCEL_REQUEST = "order.cancel.request";

    // ====== Response Topics ======
    public static final String STOCK_DECREASE_SUCCESS = "order.stock.decrease.success";
    public static final String STOCK_DECREASE_FAILURE = "order.stock.decrease.failure";
    public static final String COUPON_USE_SUCCESS = "order.coupon.use.success";
    public static final String COUPON_USE_FAILURE = "order.coupon.use.failure";
    public static final String PAYMENT_CREATE_SUCCESS = "order.payment.create.success";
    public static final String PAYMENT_CREATE_FAILURE = "order.payment.create.failure";

    private KafkaTopics() {
        throw new AssertionError("Cannot instantiate");
    }}