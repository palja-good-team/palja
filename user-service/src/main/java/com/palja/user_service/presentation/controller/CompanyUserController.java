package com.palja.user_service.presentation.controller;

import org.springframework.http.ResponseEntity;

import com.palja.common.response.ApiResponse;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.presentation.dto.request.CreateCompanyUserReq;
import com.palja.user_service.presentation.dto.request.UpdateCompanyUserStatusReq;

public interface CompanyUserController {

	ResponseEntity<ApiResponse<CreateUserRes>> create(CreateCompanyUserReq requestDto);

	ResponseEntity<ApiResponse<Void>> updateStatus(String loginId, UpdateCompanyUserStatusReq requestDto);

}
