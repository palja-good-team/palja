package com.palja.user_service.application.service;

import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;

public interface CompanyUserService {

	CreateUserRes createCompanyUser(CreateCompanyUserCommand command);

	void updateCompanyUserStatus(String currentUserLoginId, String loginId, UpdateCompanyUserStatusCommand command);

}
