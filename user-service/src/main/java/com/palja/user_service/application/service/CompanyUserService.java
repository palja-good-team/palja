package com.palja.user_service.application.service;

import com.palja.user_service.application.command.CreateCompanyUserCommand;

public interface CompanyUserService {

	void createCompanyUser(CreateCompanyUserCommand command);

}
