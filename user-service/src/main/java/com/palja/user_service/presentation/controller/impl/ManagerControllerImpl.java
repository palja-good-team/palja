package com.palja.user_service.presentation.controller.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.service.ManagerService;
import com.palja.user_service.presentation.controller.ManagerController;
import com.palja.user_service.presentation.dto.request.CreateManagerReq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/managers")
@RequiredArgsConstructor
public class ManagerControllerImpl implements ManagerController {

	private final ManagerService managerService;

	@Override
	@RequiredRole({UserRole.MASTER})
	@PostMapping
	public ResponseEntity<ApiResponse<CreateUserRes>> create(@Valid @RequestBody CreateManagerReq requestDto) {
		String currentUserLoginId = CurrentUser.getLoginId();

		CreateManagerCommand command = CreateManagerReq.of(requestDto);
		CreateUserRes responseDto = managerService.createManager(currentUserLoginId, command);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto, "관리자가 생성되었습니다."));
	}

}
