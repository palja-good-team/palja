package com.palja.user_service.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.palja.common.response.ApiResponse;
import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.service.CompanyUserService;
import com.palja.user_service.presentation.dto.request.CreateCompanyUserReq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/company-users")
@RequiredArgsConstructor
public class CompanyUserController {

	private final CompanyUserService companyUserService;

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> create(@Valid @RequestBody CreateCompanyUserReq requestDto) {
		CreateCompanyUserCommand command = CreateCompanyUserReq.of(requestDto);
		companyUserService.createCompanyUser(command);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("업체 판매자가 생성되었습니다."));
	}

}
