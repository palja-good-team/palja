package com.palja.timedeal_service.infrastructure.repository;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaTimeDealRepository extends JpaRepository<TimeDeal, UUID> {
    @Query("""
        select td
        from TimeDeal td
        join fetch td.timeDealStock ts
        where td.timeDealId = :timeDealId
          and td.deletedAt is null
    """)
    Optional<TimeDeal> findDetailByTimeDealId(UUID timeDealId);

    @Query("""
        select td
        from TimeDeal td
        join fetch td.timeDealStock ts
        where td.deletedAt is null
    """)
    Page<TimeDeal> searchTimeDeals(Pageable pageable);
}
