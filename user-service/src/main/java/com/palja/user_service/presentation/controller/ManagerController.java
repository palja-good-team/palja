package com.palja.user_service.presentation.controller;

import org.springframework.http.ResponseEntity;

import com.palja.common.response.ApiResponse;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.presentation.dto.request.CreateManagerReq;

public interface ManagerController {

	ResponseEntity<ApiResponse<CreateUserRes>> create(CreateManagerReq requestDto);

}
