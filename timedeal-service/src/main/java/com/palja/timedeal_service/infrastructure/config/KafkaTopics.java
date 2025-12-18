package com.palja.timedeal_service.infrastructure.config;

public class KafkaTopics {
    private KafkaTopics() {}

    public static final String PRODUCT_STOCK_DECREASE = "time-deal.product.stock.decrease.request";
    public static final String PRODUCT_STOCK_RESTORE = "time-deal.product.stock.restore.request";
}
