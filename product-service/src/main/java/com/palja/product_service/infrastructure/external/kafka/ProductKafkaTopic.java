package com.palja.product_service.infrastructure.external.kafka;

import lombok.Getter;

@Getter
public enum ProductKafkaTopic {

//    DECREASE_STOCK_TIMEDEAL("productstock.timedeal.deduct.res"),
//    INCREASE_STOCK_TIMEDEAL("productstock.timedeal.increase.res");
    CHANGE_PRODUCT_PRICE("product.timedeal.update.req");

    private final String topicName;

    ProductKafkaTopic(String topicName) {
        this.topicName = topicName;
    }
}
