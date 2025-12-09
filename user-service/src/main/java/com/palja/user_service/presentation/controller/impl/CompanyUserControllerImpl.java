package com.palja.user_service.presentation.controller.impl;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
import com.palja.user_service.application.command.UpdateCompanyUserCommand;
import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserDetailRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCompanyUserDetailRes;
import com.palja.user_service.application.service.CompanyUserService;
import com.palja.user_service.presentation.controller.CompanyUserController;
import com.palja.user_service.presentation.dto.request.CreateCompanyUserReq;
import com.palja.user_service.presentation.dto.request.UpdateCompanyUserReq;
import com.palja.user_service.presentation.dto.request.UpdateCompanyUserStatusReq;

import jakarta.servlet.http.HttpServletResponse;
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
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<ReadCompanyUserSummaryRes>>> getAll(
		@RequestParam(required = false) String loginId,
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String name,
		@RequestParam(required = false) String status,
		Pageable pageable
	) {
		PageResponse<ReadCompanyUserSummaryRes> pagedResponseDto = companyUserService.getAllCompanyUsers(
			CurrentUser.getLoginId(), loginId, email, name, status, pageable
		);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(pagedResponseDto, "업체 판매자 목록을 조회했습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@GetMapping("/{loginId}")
	public ResponseEntity<ApiResponse<ReadCompanyUserDetailRes>> getByLoginId(@PathVariable String loginId) {
		ReadCompanyUserDetailRes responseDto = companyUserService.getCompanyUserByLoginId(CurrentUser.getLoginId(), loginId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "업체 판매자 사용자를 조회했습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@GetMapping("/internal/{companyUserId}")
	public ResponseEntity<ApiResponse<ReadCompanyUserDetailRes>> getByCompanyUserId(@PathVariable UUID companyUserId) {
		ReadCompanyUserDetailRes responseDto = companyUserService.getCompanyUserByCompanyUserId(CurrentUser.getLoginId(), companyUserId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "업체 판매자 사용자를 조회했습니다."));
	}

	@Override
	@RequiredRole({UserRole.COMPANY_USER})
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<ReadCompanyUserDetailRes>> getMe() {
		ReadCompanyUserDetailRes responseDto = companyUserService.getMe(CurrentUser.getLoginId());

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "업체 판매자 사용자를 조회했습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@PutMapping("/{loginId}")
	public ResponseEntity<ApiResponse<UpdateCompanyUserDetailRes>> updateByLoginId(
		@PathVariable String loginId, @Valid @RequestBody UpdateCompanyUserReq requestDto
	) {
		UpdateCompanyUserCommand command = UpdateCompanyUserReq.of(requestDto);
		UpdateCompanyUserDetailRes responseDto = companyUserService.updateCompanyUserByLoginId(CurrentUser.getLoginId(), loginId, command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "업체 판매자가 수정되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.COMPANY_USER})
	@PutMapping("/me")
	public ResponseEntity<ApiResponse<UpdateCompanyUserDetailRes>> updateMe(@Valid @RequestBody UpdateCompanyUserReq requestDto) {
		UpdateCompanyUserCommand command = UpdateCompanyUserReq.of(requestDto);
		UpdateCompanyUserDetailRes responseDto = companyUserService.updateMe(CurrentUser.getLoginId(), command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "업체 판매자가 수정되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@PutMapping("/{loginId}/status")
	public ResponseEntity<ApiResponse<Void>> updateStatus(
		@PathVariable("loginId") String loginId, @Valid @RequestBody UpdateCompanyUserStatusReq requestDto
	) {
		UpdateCompanyUserStatusCommand command = UpdateCompanyUserStatusReq.of(requestDto);
		companyUserService.updateCompanyUserStatus(CurrentUser.getLoginId(), loginId, command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("업체 판매자의 상태가 수정되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@DeleteMapping("/{loginId}")
	public ResponseEntity<ApiResponse<Void>> deleteByLoginId(@PathVariable String loginId) {
		companyUserService.deleteCompanyUserByLoginId(CurrentUser.getLoginId(), loginId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("업체 판매자가 삭제되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.COMPANY_USER})
	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<Void>> deleteMe(
		@RequestHeader(value = "Authorization", required = false) String accessToken, HttpServletResponse response
	) {
		companyUserService.deleteMe(accessToken, CurrentUser.getLoginId());

		ResponseCookie cookie = ResponseCookie
			.from("refresh_token", "")
			.path("/")
			.httpOnly(true)
			.secure(false)
			.maxAge(0)
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("업체 판매자가 삭제되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@DeleteMapping("/{loginId}/reject")
	public ResponseEntity<ApiResponse<Void>> reject(@PathVariable String loginId) {
		companyUserService.rejectCompanyUser(CurrentUser.getLoginId(), loginId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("업체 판매자 가입이 거절되었습니다."));
	}

}
