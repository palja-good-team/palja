package com.palja.user_service.application.service;

import com.palja.user_service.application.command.CreateManagerCommand;

public interface ManagerService {

	void createManager(CreateManagerCommand command);

}
