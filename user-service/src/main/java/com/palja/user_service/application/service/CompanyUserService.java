package com.palja.user_service.application.service;

import java.util.UUID;

import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;

public interface CompanyUserService {

	void createCompanyUser(CreateCompanyUserCommand command);

	void updateCompanyUserStatus(UUID companyUserId, UpdateCompanyUserStatusCommand command);

}
