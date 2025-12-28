package com.palja.timedeal_service.application.event.publisher;

import com.palja.timedeal_service.application.event.dto.request.out.ProductStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.dto.request.out.ProductStockRestoreEventReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeDealInternalEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishProductStockDecrease(UUID timeDealId, UUID productId, long quantity) {
        log.info("[InternalEvent] 상품 재고 차감 요청 발행 timeDealId = {}, productId = {} 차감 수량 = {}",
                timeDealId, productId, quantity);

        ProductStockDecreaseEventReq event = ProductStockDecreaseEventReq.of(timeDealId, productId, quantity);

        applicationEventPublisher.publishEvent(event);
    }

    public void publishProductStockRestore(UUID timeDealId, UUID productId, long quantity) {
        log.info("[InternalEvent] 상품 재고 복구 요청 발행 timeDealId = {}, productId = {} 복구 수량 = {}",
                timeDealId, productId, quantity);

        ProductStockRestoreEventReq event = ProductStockRestoreEventReq.of(timeDealId, productId, quantity);

        applicationEventPublisher.publishEvent(event);
    }
}
