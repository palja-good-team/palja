package com.palja.timedeal_service.infrastructure.config.kafka;

public class KafkaTopics {
    private KafkaTopics() {}

    // publish
    public static final String PRODUCT_STOCK_DECREASE = "time-deal.product.stock.decrease.request";
    public static final String PRODUCT_STOCK_RESTORE = "time-deal.product.stock.restore.request";
    public static final String ORDER_STOCK_DEDUCT_SUCCESS = "order.stock.deduct.success";
    public static final String ORDER_STOCK_DEDUCT_FAILURE = "order.stock.deduct.failure";

    // subscribe
    public static final String ORDER_STOCK_DEDUCT_REQUEST = "order.stock.deduct.request";
    public static final String ORDER_STOCK_RESTORE_REQUEST = "order.stock.restore.request";
}
