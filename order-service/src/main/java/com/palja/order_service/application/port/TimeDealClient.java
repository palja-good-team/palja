package com.palja.order_service.application.port;

import com.palja.order_service.application.dto.external.TimeDealRes;

import java.util.UUID;

public interface TimeDealClient {

    // 타임딜 조회
    TimeDealRes getTimeDeal(UUID timeDealId);

    // 타임딜 재고 차감
    void deductTimeDealStock(UUID timeDealId, Long quantity);

    // 타임딜 재고 복구
    void restoreTimeDealStock(UUID timeDealId, Long quantity);
}