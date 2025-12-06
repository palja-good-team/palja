package com.palja.user_service.presentation.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadManagerDetailRes;
import com.palja.user_service.application.dto.response.ReadManagerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateManagerDetailRes;
import com.palja.user_service.presentation.dto.request.CreateManagerReq;
import com.palja.user_service.presentation.dto.request.UpdateManagerReq;

public interface ManagerController {

	ResponseEntity<ApiResponse<CreateUserRes>> create(CreateManagerReq requestDto);

	ResponseEntity<ApiResponse<PageResponse<ReadManagerSummaryRes>>> getAll(
		String loginId, String email, String name, Pageable pageable
	);

	ResponseEntity<ApiResponse<ReadManagerDetailRes>> getByLoginId(String loginId);

	ResponseEntity<ApiResponse<ReadManagerDetailRes>> getByUserId(Long userId);

	ResponseEntity<ApiResponse<ReadManagerDetailRes>> getMe();

	ResponseEntity<ApiResponse<UpdateManagerDetailRes>> updateByLoginId(String loginId, UpdateManagerReq requestDto);

	ResponseEntity<ApiResponse<UpdateManagerDetailRes>> updateMe(UpdateManagerReq requestDto);

	ResponseEntity<ApiResponse<Void>> deleteByLoginId(String loginId);

}
