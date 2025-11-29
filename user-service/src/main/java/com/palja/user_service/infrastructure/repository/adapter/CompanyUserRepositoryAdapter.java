package com.palja.user_service.infrastructure.repository.adapter;

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

}
