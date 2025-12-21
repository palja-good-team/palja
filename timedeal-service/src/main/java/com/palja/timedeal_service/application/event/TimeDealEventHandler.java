package com.palja.timedeal_service.application.event;

import com.palja.timedeal_service.application.event.internal.TimeDealStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.internal.TimeDealStockIncreaseEventReq;
import com.palja.timedeal_service.application.port.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealEventHandler {

    private final ProductEventPublisher productEventPort;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTimeDealStockIncreased(TimeDealStockIncreaseEventReq event) {
        log.info("타임딜 생성 완료 -> 상품 재고 차감 요청 발행 timeDealId = {}, productId = {} 차감 수량 = {}",
                event.getTimeDealId(), event.getProductId(), event.getQuantity());

        productEventPort.publishDecrease(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTimeDealStockDecreased(TimeDealStockDecreaseEventReq event) {
        log.info("타임딜 종료 완료 -> 남은 재고 복구 요청 발행 TimeDealId = {}, productId = {} 복구 수량 = {}",
                event.getTimeDealId(), event.getProductId(), event.getQuantity());

        productEventPort.publishRestore(event);
    }
}