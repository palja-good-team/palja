package com.palja.timedeal_service.domain.repository;

import com.palja.timedeal_service.domain.entity.TimeDeal;

public interface TimeDealRepository {
    TimeDeal save(TimeDeal timeDeal);
}
