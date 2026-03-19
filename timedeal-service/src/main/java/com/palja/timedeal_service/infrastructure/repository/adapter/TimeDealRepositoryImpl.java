package com.palja.timedeal_service.infrastructure.repository.adapter;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.repository.TimeDealRepository;
import com.palja.timedeal_service.domain.vo.TimeDealStatus;
import com.palja.timedeal_service.infrastructure.repository.JpaTimeDealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class TimeDealRepositoryImpl implements TimeDealRepository {

    private final JpaTimeDealRepository jpaTimeDealRepository;

    @Override
    public TimeDeal save(TimeDeal timeDeal) {
        return jpaTimeDealRepository.save(timeDeal);
    }

    @Override
    public Optional<TimeDeal> findByTimeDealId(UUID timeDealId) {
        return jpaTimeDealRepository.findByTimeDealId(timeDealId);
    }

    @Override
    public Page<TimeDeal> searchTimeDeals(Pageable pageable) {
        return jpaTimeDealRepository.searchTimeDeals(pageable);
    }

    @Override
    public List<TimeDeal> findAllByCompanyUserId(UUID companyUserId, TimeDealStatus status, LocalDateTime now) {
        return jpaTimeDealRepository.findAllByCompanyUserId(companyUserId, status, now);
    }

    @Override
    public List<TimeDeal> findAllPendingByProductId(UUID productId, TimeDealStatus status) {
        return jpaTimeDealRepository.findAllPendingByProductId(productId, status);
    }
}
