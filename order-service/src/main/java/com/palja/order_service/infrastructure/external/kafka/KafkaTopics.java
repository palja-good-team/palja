package com.palja.order_service.infrastructure.external.kafka;

/**
 * Kafka Topic 상수 정의
 */
public class KafkaTopics {

    // ====== Request Topics ======
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

    // ====== external Request Topics ======
    // ===== payment =====
    public static final String PAYMENT_ORDER_APPROVE_SUCCESS  = "payment.order.approve.success";
    public static final String PAYMENT_ORDER_CANCEL_SUCCESS = "payment.order.cancel.success";

// ====== DLT Topics (자동 생성) ======
    // 규칙: {원본토픽}.DLT
    //
    // 예시:
    // - saga.start.request.DLT
    // - order.stock.decrease.success.DLT
    // - order.stock.decrease.failure.DLT
    // - order.coupon.use.success.DLT
    // - order.coupon.use.failure.DLT
    // - order.payment.create.success.DLT
    // - order.payment.create.failure.DLT

    private KafkaTopics() {
        throw new AssertionError("Cannot instantiate");
    }
}