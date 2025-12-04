package com.palja.user_service.infrastructure.repository.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.palja.user_service.domain.entity.CompanyUser;
import com.palja.user_service.domain.repository.CompanyUserRepository;
import com.palja.user_service.infrastructure.repository.JpaCompanyUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompanyUserRepositoryImpl implements CompanyUserRepository {

	private final JpaCompanyUserRepository jpaCompanyUserRepository;

	@Override
	public CompanyUser save(CompanyUser companyUser) {
		return jpaCompanyUserRepository.save(companyUser);
	}

	@Override
	public Optional<CompanyUser> findByIdAndDeletedAtIsNull(UUID companyUserId) {
		return jpaCompanyUserRepository.findByIdAndDeletedAtIsNull(companyUserId);
	}

}
