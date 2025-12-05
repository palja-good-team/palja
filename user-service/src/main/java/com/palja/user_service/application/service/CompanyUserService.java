package com.palja.user_service.application.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.palja.common.response.PageResponse;
import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserDetailRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserSummaryRes;

public interface CompanyUserService {

	CreateUserRes createCompanyUser(CreateCompanyUserCommand command);

	void updateCompanyUserStatus(String currentUserLoginId, String loginId, UpdateCompanyUserStatusCommand command);

	PageResponse<ReadCompanyUserSummaryRes> getAllCompanyUsers(
		String currentUserLoginId, String loginId, String email, String name, String status, Pageable pageable
	);

	ReadCompanyUserDetailRes getCustomerByLoginId(String currentUserLoginId, String loginId);

	ReadCompanyUserDetailRes getCustomerByCompanyUserId(String currentUserLoginId, UUID companyUserId);

}

