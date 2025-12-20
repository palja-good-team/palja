package com.palja.product_service.infrastructure.external.kafka;

import lombok.Getter;

@Getter
public class ProductKafkaTopic {

    public static final String DECREASE_STOCK_TIMEDEAL = "time-deal.product.stock.decrease.request";
    public static final String DECREASE_STOCK_TIMEDEAL_ERROR = "product.stock.timedeal.decrease.failure";
    public static final String INCREASE_STOCK_TIMEDEAL = "time-deal.product.stock.restore.request";
    public static final String CHANGE_PRODUCT_PRICE = "product.timedeal.price-update.request";

    public static final String SALE_STOCK_ORDER = "order.stock.deduct.request";
    public static final String RESTORE_STOCK_ORDER = "order.stock.restore.request";
    public static final String ORDER_CANCEL = "order.cancel.request";

}
