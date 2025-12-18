package com.palja.order_service.infrastructure.external.kafka;

/**
 * Kafka Topic 상수 정의
 */
public class KafkaTopics {

    // ========== Saga start (Order -> Order Orchestrator) ==========
    public static final String SAGA_START_REQUEST = "order.saga.start.request";

    // ========== Order Create Saga ==========
    // Stock (Order -> Product, Product -> Order)
    public static final String STOCK_DEDUCT_REQUEST = "order.stock.deduct.request";
    // 응답
    public static final String STOCK_DEDUCT_SUCCESS = "order.stock.deduct.success";
    public static final String STOCK_DEDUCT_FAILURE = "order.stock.deduct.failure";

    // Coupon (Order -> Coupon, Coupon -> Order)
    public static final String COUPON_USE_REQUEST = "order.coupon.use.request";
    // 응답
    public static final String COUPON_USE_SUCCESS = "order.coupon.use.success";
    public static final String COUPON_USE_FAILURE = "order.coupon.use.failure";

    // Payment (Order -> Payment, Payment -> Order)
    public static final String PAYMENT_CREATE_REQUEST = "order.payment.create.request";
    // 응답
    public static final String PAYMENT_CREATE_SUCCESS = "order.payment.create.success";
    public static final String PAYMENT_CREATE_FAILURE = "order.payment.create.failure";

    // ========== Order Create Saga (보상) ==========
    // Stock Restore (Order -> Product, Product -> Order)
    public static final String STOCK_RESTORE_REQUEST = "order.stock.restore.request";

    // Coupon Cancel (Order -> Coupon, Coupon -> Order)
    public static final String COUPON_CANCEL_REQUEST = "order.coupon.cancel.request";

    // ========== Order Cancel ==========
    public static final String ORDER_CANCEL_REQUEST = "order.cancel.request";


    private KafkaTopics() {
        throw new AssertionError("Cannot instantiate");
    }}