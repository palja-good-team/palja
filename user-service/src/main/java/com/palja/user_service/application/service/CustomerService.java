package com.palja.user_service.application.service;

import org.springframework.data.domain.Pageable;

import com.palja.common.response.PageResponse;
import com.palja.user_service.application.command.CreateCustomerCommand;
import com.palja.user_service.application.command.UpdateCustomerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCustomerDetailRes;
import com.palja.user_service.application.dto.response.ReadCustomerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCustomerDetailRes;

public interface CustomerService {

	CreateUserRes createCustomer(CreateCustomerCommand command);

	PageResponse<ReadCustomerSummaryRes> getAllCustomers(String currentUserLoginId, String loginId, String email, String name, Pageable pageable);

	ReadCustomerDetailRes getCustomerByLoginId(String currentUserLoginId, String loginId);

	ReadCustomerDetailRes getCustomerByUserId(String currentUserLoginId, Long userId);

	ReadCustomerDetailRes getMe(String currentUserLoginId);

	UpdateCustomerDetailRes updateCustomerByLoginId(String currentUserLoginId, String loginId, UpdateCustomerCommand command);

}
