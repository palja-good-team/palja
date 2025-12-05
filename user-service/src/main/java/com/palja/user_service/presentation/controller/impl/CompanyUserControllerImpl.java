package com.palja.user_service.presentation.controller.impl;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.palja.common.annotation.RequiredAnonymous;
import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserSummaryRes;
import com.palja.user_service.application.service.CompanyUserService;
import com.palja.user_service.presentation.controller.CompanyUserController;
import com.palja.user_service.presentation.dto.request.CreateCompanyUserReq;
import com.palja.user_service.presentation.dto.request.UpdateCompanyUserStatusReq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/company-users")
@RequiredArgsConstructor
public class CompanyUserControllerImpl implements CompanyUserController {

	private final CompanyUserService companyUserService;

	@Override
	@RequiredAnonymous
	@PostMapping
	public ResponseEntity<ApiResponse<CreateUserRes>> create(@Valid @RequestBody CreateCompanyUserReq requestDto) {
		CreateCompanyUserCommand command = CreateCompanyUserReq.of(requestDto);
		CreateUserRes responseDto = companyUserService.createCompanyUser(command);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto, "업체 판매자가 생성되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@PutMapping("/{loginId}/status")
	public ResponseEntity<ApiResponse<Void>> updateStatus(
		@PathVariable("loginId") String loginId, @Valid @RequestBody UpdateCompanyUserStatusReq requestDto
	) {
		String currentUserLoginId = CurrentUser.getLoginId();

		UpdateCompanyUserStatusCommand command = UpdateCompanyUserStatusReq.of(requestDto);
		companyUserService.updateCompanyUserStatus(currentUserLoginId, loginId, command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("업체 판매자의 상태가 수정되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<ReadCompanyUserSummaryRes>>> getAll(
		@RequestParam(required = false) String loginId,
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String status,
		Pageable pageable
	) {
		String currentUserLoginId = CurrentUser.getLoginId();

		PageResponse<ReadCompanyUserSummaryRes> pagedResponseDto = companyUserService.getAllCompanyUsers(
			currentUserLoginId, loginId, email, name, status, pageable
		);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(pagedResponseDto, "업체 판매자 목록을 조회했습니다."));
	}

}
