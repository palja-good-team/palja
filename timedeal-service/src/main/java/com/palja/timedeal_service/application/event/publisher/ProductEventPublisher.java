package com.palja.timedeal_service.application.event.publisher;

import com.palja.timedeal_service.application.event.impl.TimeDealStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.impl.TimeDealStockIncreaseEventReq;

public interface ProductEventPublisher {
    void publishDecrease(TimeDealStockIncreaseEventReq event);
    void publishRestore(TimeDealStockDecreaseEventReq event);
}
