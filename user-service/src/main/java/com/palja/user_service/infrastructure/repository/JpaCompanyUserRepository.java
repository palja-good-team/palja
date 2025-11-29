package com.palja.user_service.infrastructure.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.palja.user_service.domain.entity.CompanyUser;

public interface JpaCompanyUserRepository extends JpaRepository<CompanyUser, UUID> {

	boolean existsByEmailAndDeletedAtIsNull(String email);

	@Query("""
		SELECT cu
		FROM CompanyUser cu
		JOIN FETCH cu.user
		WHERE cu.id = :companyUserId
		AND cu.deletedAt IS NULL
	""")
	Optional<CompanyUser> findByIdAndDeletedAtIsNull(@Param("companyUserId") UUID companyUserId);

}
