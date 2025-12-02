package com.palja.user_service.application.service;

import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;

public interface ManagerService {

	CreateUserRes createManager(CreateManagerCommand command);

}
