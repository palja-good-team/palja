package com.palja.product_service.application.event.publisher;

import com.palja.product_service.application.event.ChangePriceEvent;

public interface ProductEventPublisher {

    void handleChangePriceEvent(ChangePriceEvent event);
}
