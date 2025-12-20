package com.palja.product_service.application.event.publisher;

import com.palja.product_service.application.event.ChangePriceEvent;
import com.palja.product_service.application.event.DecreaseStockTimeDealErrorEvent;
import com.palja.product_service.application.event.SaleProductErrorEvent;

public interface ProductEventPublisher {

    void handleChangePriceEvent(ChangePriceEvent event);

    void handleSaleProductErrorEvent(SaleProductErrorEvent event);

    void handleDecreaseStockTimeDealErrorEvent(DecreaseStockTimeDealErrorEvent event);
}
