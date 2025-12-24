package com.palja.product_service.application.port;

import com.palja.product_service.application.event.dto.request.ChangePriceEventReq;
import com.palja.product_service.application.event.dto.request.DecreaseStockTimeDealErrorEventReq;
import com.palja.product_service.application.event.dto.request.SaleProductErrorEventReq;

public interface ProductEventPublisher {

    void handleChangePriceEvent(ChangePriceEventReq event);

    void handleSaleProductErrorEvent(SaleProductErrorEventReq event);

    void handleDecreaseStockTimeDealErrorEvent(DecreaseStockTimeDealErrorEventReq event);
}
