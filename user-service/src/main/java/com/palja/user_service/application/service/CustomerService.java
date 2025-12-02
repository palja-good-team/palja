package com.palja.user_service.application.service;

import com.palja.user_service.application.command.CreateCustomerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;

public interface CustomerService {

	CreateUserRes createCustomer(CreateCustomerCommand command);

}
