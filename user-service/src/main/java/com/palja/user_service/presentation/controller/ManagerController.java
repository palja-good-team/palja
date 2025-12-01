package com.palja.user_service.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.palja.common.response.ApiResponse;
import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.application.service.ManagerService;
import com.palja.user_service.presentation.dto.request.CreateManagerReq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/managers")
@RequiredArgsConstructor
public class ManagerController {

	private final ManagerService managerService;

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody CreateManagerReq requestDto) {
		CreateManagerCommand command = CreateManagerReq.of(requestDto);
		managerService.createManager(command);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("관리자가 생성되었습니다."));
	}

}
