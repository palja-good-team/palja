package com.palja.order_service.application.service;

import com.palja.order_service.application.dto.TimeDealRes;

import java.util.UUID;

public interface TimeDealService {

    TimeDealRes getTimeDeal(UUID timeDealId, int quantity);

    void deductTimeDealStock(UUID timeDealId, int quantity);

    void restoreTimeDealStock(UUID timeDealId, int quantity);
}