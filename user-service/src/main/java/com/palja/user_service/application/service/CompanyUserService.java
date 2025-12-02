package com.palja.user_service.application.service;

import java.util.UUID;

import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;

public interface CompanyUserService {

	CreateUserRes createCompanyUser(CreateCompanyUserCommand command);

	void updateCompanyUserStatus(UUID companyUserId, UpdateCompanyUserStatusCommand command);

}
