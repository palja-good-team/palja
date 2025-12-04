package com.palja.timedeal_service.infrastructure.repository.adapter;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import com.palja.timedeal_service.infrastructure.repository.JpaTimeDealRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class TimeDealRepositoryAdapter implements TimeDealRepository {

    private final JpaTimeDealRepository jpaTimeDealRepository;

    @Override
    public TimeDeal save(TimeDeal timeDeal) {
        return jpaTimeDealRepository.save(timeDeal);
    }

    @Override
    public Optional<TimeDeal> findDetailByTimeDealId(UUID timeDealId) {
        return jpaTimeDealRepository.findDetailByTimeDealId(timeDealId);
    }
}
