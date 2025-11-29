package com.palja.user_service.application.service;

import com.palja.user_service.application.command.CreateCustomerCommand;

public interface CustomerService {

	void createCustomer(CreateCustomerCommand command);

}
