package com.palja.user_service.infrastructure.repository.adapter;

import org.springframework.stereotype.Component;

import com.palja.user_service.domain.repository.CompanyUserRepository;
import com.palja.user_service.infrastructure.repository.JpaCompanyUserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompanyUserRepositoryAdapter implements CompanyUserRepository {

	private final JpaCompanyUserRepository jpaCompanyUserRepository;

}
