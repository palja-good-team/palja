package com.palja.coupon_service.infrastructure.external.kafka;

public class KafkaTopics {
    public static final String COUPON_USE_REQUEST = "order.coupon.use.request";
    public static final String COUPON_USE_SUCCESS = "order.coupon.use.success";
    public static final String COUPON_USE_FAILURE = "order.coupon.use.failure";

    public static final String COUPON_CANCEL_REQUEST = "order.coupon.cancel.request";
    public static final String COUPON_CANCEL_SUCCESS = "order.coupon.cancel.success";
    public static final String COUPON_CANCEL_FAILURE = "order.coupon.cancel.failure";
}
