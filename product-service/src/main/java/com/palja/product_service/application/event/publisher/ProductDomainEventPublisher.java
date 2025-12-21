package com.palja.product_service.application.event.publisher;

import com.palja.product_service.application.event.dto.request.ChangePriceEventReq;
import com.palja.product_service.application.event.dto.request.DecreaseStockTimeDealErrorEventReq;
import com.palja.product_service.application.event.dto.request.SaleProductErrorEventReq;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishPriceChangeEvent(ChangePriceEventReq req) {

        applicationEventPublisher.publishEvent(req);
    }

    public void publishSaleEvent(SaleProductErrorEventReq req) {

        applicationEventPublisher.publishEvent(req);
    }

    public void publishCreateTimeDealEvent(DecreaseStockTimeDealErrorEventReq req) {

        applicationEventPublisher.publishEvent(req);
    }
}
