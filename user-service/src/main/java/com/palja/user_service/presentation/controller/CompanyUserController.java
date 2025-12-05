package com.palja.user_service.presentation.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserDetailRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCompanyUserDetailRes;
import com.palja.user_service.presentation.dto.request.CreateCompanyUserReq;
import com.palja.user_service.presentation.dto.request.UpdateCompanyUserReq;
import com.palja.user_service.presentation.dto.request.UpdateCompanyUserStatusReq;

public interface CompanyUserController {

	ResponseEntity<ApiResponse<CreateUserRes>> create(CreateCompanyUserReq requestDto);

	ResponseEntity<ApiResponse<Void>> updateStatus(String loginId, UpdateCompanyUserStatusReq requestDto);

	ResponseEntity<ApiResponse<PageResponse<ReadCompanyUserSummaryRes>>> getAll(
		String loginId, String email, String name, String status, Pageable pageable
	);

	ResponseEntity<ApiResponse<ReadCompanyUserDetailRes>> getByLoginId(String loginId);

	ResponseEntity<ApiResponse<ReadCompanyUserDetailRes>> getByCompanyUserId(UUID companyUserId);

	ResponseEntity<ApiResponse<ReadCompanyUserDetailRes>> getMe();

	ResponseEntity<ApiResponse<UpdateCompanyUserDetailRes>> updateByLoginId(String loginId, UpdateCompanyUserReq requestDto);

}
