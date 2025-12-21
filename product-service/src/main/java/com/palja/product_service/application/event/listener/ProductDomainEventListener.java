package com.palja.product_service.application.event.listener;

import com.palja.product_service.application.event.dto.request.ChangePriceEventReq;
import com.palja.product_service.application.event.dto.request.DecreaseStockTimeDealErrorEventReq;
import com.palja.product_service.application.event.dto.request.SaleProductErrorEventReq;
import com.palja.product_service.application.port.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductDomainEventListener {

    private final ProductEventPublisher productEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChangePriceEvent(ChangePriceEventReq event) {

        productEventPublisher.handleChangePriceEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleSaleProductErrorEvent(SaleProductErrorEventReq event) {

        productEventPublisher.handleSaleProductErrorEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleDecreaseStockTimeDealErrorEvent(DecreaseStockTimeDealErrorEventReq event) {

        productEventPublisher.handleDecreaseStockTimeDealErrorEvent(event);
    }
}
