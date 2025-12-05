package com.palja.user_service.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.palja.user_service.domain.entity.CompanyUser;

public interface CompanyUserRepository {

	CompanyUser save(CompanyUser companyUser);

	Optional<CompanyUser> findByIdAndDeletedAtIsNull(UUID companyUserId);

	Optional<CompanyUser> findByLoginIdAndDeletedAtIsNull(String loginId);

}
