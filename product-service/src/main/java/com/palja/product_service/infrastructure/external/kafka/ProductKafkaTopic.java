package com.palja.product_service.infrastructure.external.kafka;

import lombok.Getter;

@Getter
public class ProductKafkaTopic {

    public static final String DECREASE_STOCK_TIMEDEAL = "productstock.timedeal.deduct.res";
    public static final String DECREASE_STOCK_TIMEDEAL_ERROR = "productstock.timedeal.deduct.failure";
    public static final String INCREASE_STOCK_TIMEDEAL = "productstock.timedeal.increase.res";
    public static final String CHANGE_PRODUCT_PRICE = "product.timedeal.update.req";

}
