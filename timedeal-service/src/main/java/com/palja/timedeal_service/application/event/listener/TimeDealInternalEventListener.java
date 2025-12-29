package com.palja.timedeal_service.application.event.listener;

import com.palja.timedeal_service.application.event.dto.request.out.ProductStockRestoreEventReq;
import com.palja.timedeal_service.application.event.dto.request.out.ProductStockDecreaseEventReq;
import com.palja.timedeal_service.application.port.TimeDealEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealInternalEventListener {

    private final TimeDealEventPublisher timeDealEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductStockDecrease(ProductStockDecreaseEventReq event) {
        log.info("[Kafka-Publish] 상품 재고 차감 요청 발행 timeDealId = {}, productId = {} 차감 수량 = {}",
                event.getTimeDealId(), event.getProductId(), event.getQuantity());

        timeDealEventPublisher.publishProductStockDecrease(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductStockRestore(ProductStockRestoreEventReq event) {
        log.info("[Kafka-Publish] 상품 재고 복구 요청 발행 TimeDealId = {}, productId = {} 복구 수량 = {}",
                event.getTimeDealId(), event.getProductId(), event.getQuantity());

        timeDealEventPublisher.publishProductStockRestore(event);
    }
}
