package com.palja.timedeal_service.domain.repository;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import com.palja.timedeal_service.domain.vo.TimeDealStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TimeDealRepository {
    TimeDeal save(TimeDeal timeDeal);
    Optional<TimeDeal> findDetailByTimeDealId(UUID timeDealId);
    Page<TimeDeal> searchTimeDeals(Pageable pageable);
    List<TimeDeal> findAllByCompanyUserId(UUID companyUserId, TimeDealStatus status, LocalDateTime now);
}
