package com.palja.timedeal_service.infrastructure.repository;

import com.palja.timedeal_service.domain.entity.TimeDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaTimeDealRepository extends JpaRepository<TimeDeal, UUID> {
}
