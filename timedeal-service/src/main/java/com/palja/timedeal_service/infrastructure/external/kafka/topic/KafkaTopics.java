package com.palja.timedeal_service.infrastructure.external.kafka.topic;

public class KafkaTopics {
    private KafkaTopics() {}

    // publish
    public static final String PRODUCT_STOCK_DECREASE = "time-deal.product.stock.decrease.request";
    public static final String PRODUCT_STOCK_RESTORE = "time-deal.product.stock.restore.request";
    public static final String ORDER_STOCK_DECREASE_SUCCESS = "order.stock.decrease.success";
    public static final String ORDER_STOCK_DECREASE_FAILURE = "order.stock.decrease.failure";

    // listen
    public static final String ORDER_STOCK_DECREASE_REQUEST = "order.stock.decrease.request";
    public static final String ORDER_STOCK_RESTORE_REQUEST = "order.stock.restore.request";
    public static final String USER_COMPANY_USER_DELETE_REQUEST = "user.company-user.delete.request";
}
