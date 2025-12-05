package com.palja.user_service.presentation.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCustomerDetailRes;
import com.palja.user_service.application.dto.response.ReadCustomerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCustomerDetailRes;
import com.palja.user_service.presentation.dto.request.CreateCustomerReq;
import com.palja.user_service.presentation.dto.request.UpdateCustomerReq;

public interface CustomerController {

	ResponseEntity<ApiResponse<CreateUserRes>> create(CreateCustomerReq requestDto);

	ResponseEntity<ApiResponse<PageResponse<ReadCustomerSummaryRes>>> getAll(
		String loginId, String email, String name, Pageable pageable
	);

	ResponseEntity<ApiResponse<ReadCustomerDetailRes>> getByLoginId(String loginId);

	ResponseEntity<ApiResponse<ReadCustomerDetailRes>> getByUserId(Long userId);

	ResponseEntity<ApiResponse<ReadCustomerDetailRes>> getMe();

	ResponseEntity<ApiResponse<UpdateCustomerDetailRes>> updateByLoginId(String loginId, UpdateCustomerReq requestDto);

}
