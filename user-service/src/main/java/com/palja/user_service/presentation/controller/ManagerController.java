package com.palja.user_service.presentation.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadManagerDetailRes;
import com.palja.user_service.application.dto.response.ReadManagerSummaryRes;
import com.palja.user_service.presentation.dto.request.CreateManagerReq;

public interface ManagerController {

	ResponseEntity<ApiResponse<CreateUserRes>> create(CreateManagerReq requestDto);

	ResponseEntity<ApiResponse<PageResponse<ReadManagerSummaryRes>>> getAll(
		String loginId, String email, String name, Pageable pageable
	);

	ResponseEntity<ApiResponse<ReadManagerDetailRes>> getByLoginId(String loginId);

	ResponseEntity<ApiResponse<ReadManagerDetailRes>> getByUserId(Long userId);

}
