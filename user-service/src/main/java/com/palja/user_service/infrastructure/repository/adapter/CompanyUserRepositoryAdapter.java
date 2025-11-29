package com.palja.user_service.infrastructure.repository.adapter;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.palja.user_service.domain.entity.CompanyUser;
import com.palja.user_service.domain.repository.CompanyUserRepository;
import com.palja.user_service.infrastructure.repository.JpaCompanyUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompanyUserRepositoryAdapter implements CompanyUserRepository {

	private final JpaCompanyUserRepository jpaCompanyUserRepository;

	@Override
	public CompanyUser save(CompanyUser companyUser) {
		return jpaCompanyUserRepository.save(companyUser);
	}

	@Override
	public boolean existsByEmailAndDeletedAtIsNull(String email) {
		return jpaCompanyUserRepository.existsByEmailAndDeletedAtIsNull(email);
	}

	@Override
	public Optional<CompanyUser> findByIdAndDeletedAtIsNull(UUID companyUserId) {
		return jpaCompanyUserRepository.findByIdAndDeletedAtIsNull(companyUserId);
	}

}
