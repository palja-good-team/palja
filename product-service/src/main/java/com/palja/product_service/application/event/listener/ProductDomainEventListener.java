package com.palja.product_service.application.event.listener;

import com.palja.product_service.application.event.ChangePriceEvent;
import com.palja.product_service.application.event.DecreaseStockTimeDealErrorEvent;
import com.palja.product_service.application.event.SaleProductErrorEvent;
import com.palja.product_service.application.event.publisher.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ProductDomainEventListener {

    private final ProductEventPublisher productEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChangePriceEvent(ChangePriceEvent event) {

        productEventPublisher.handleChangePriceEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleSaleProductErrorEvent(SaleProductErrorEvent event) {

        productEventPublisher.handleSaleProductErrorEvent(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleStockTimeDealErrorEvent(DecreaseStockTimeDealErrorEvent event) {

        productEventPublisher.handleDecreaseStockTimeDealErrorEvent(event);
    }
}
