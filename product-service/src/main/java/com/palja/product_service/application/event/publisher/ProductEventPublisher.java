package com.palja.product_service.application.event.publisher;

import com.palja.product_service.application.event.ChangePriceEvent;
import com.palja.product_service.application.event.DecreaseStockErrorEvent;

public interface ProductEventPublisher {

    void handleChangePriceEvent(ChangePriceEvent event);

    void handleDecreaseStockErrorEvent(DecreaseStockErrorEvent event);
}
