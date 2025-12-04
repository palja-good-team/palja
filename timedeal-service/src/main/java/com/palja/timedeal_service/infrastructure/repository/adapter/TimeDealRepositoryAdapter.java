package com.palja.timedeal_service.infrastructure.repository.adapter;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import com.palja.timedeal_service.infrastructure.repository.JpaTimeDealRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TimeDealRepositoryAdapter implements TimeDealRepository {

    private final JpaTimeDealRepository jpaTimeDealRepository;

    @Override
    public TimeDeal save(TimeDeal timeDeal) {
        return jpaTimeDealRepository.save(timeDeal);
    }
}
