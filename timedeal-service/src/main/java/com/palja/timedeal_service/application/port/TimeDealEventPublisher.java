package com.palja.timedeal_service.application.port;

import com.palja.timedeal_service.application.event.dto.request.out.ProductStockRestoreEventReq;
import com.palja.timedeal_service.application.event.dto.request.out.ProductStockDecreaseEventReq;

public interface TimeDealEventPublisher {
    void publishProductStockDecrease(ProductStockDecreaseEventReq event);
    void publishProductStockRestore(ProductStockRestoreEventReq event);
}
