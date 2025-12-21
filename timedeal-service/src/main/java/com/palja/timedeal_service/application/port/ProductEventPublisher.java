package com.palja.timedeal_service.application.port;

import com.palja.timedeal_service.application.event.internal.TimeDealStockDecreaseEventReq;
import com.palja.timedeal_service.application.event.internal.TimeDealStockIncreaseEventReq;

public interface ProductEventPublisher {
    void publishDecrease(TimeDealStockIncreaseEventReq event);
    void publishRestore(TimeDealStockDecreaseEventReq event);
}
