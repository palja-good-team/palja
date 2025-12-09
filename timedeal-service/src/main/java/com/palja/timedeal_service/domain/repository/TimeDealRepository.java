package com.palja.timedeal_service.domain.repository;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface TimeDealRepository {
    TimeDeal save(TimeDeal timeDeal);
    Optional<TimeDeal> findDetailByTimeDealId(UUID timeDealId);
    Page<TimeDeal> searchTimeDeals(Pageable pageable);
}
