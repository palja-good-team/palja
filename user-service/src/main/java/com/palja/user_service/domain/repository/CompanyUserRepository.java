package com.palja.user_service.domain.repository;

import com.palja.user_service.domain.entity.CompanyUser;

public interface CompanyUserRepository {

	CompanyUser save(CompanyUser companyUser);

	boolean existsByEmailAndDeletedAtIsNull(String email);

}
