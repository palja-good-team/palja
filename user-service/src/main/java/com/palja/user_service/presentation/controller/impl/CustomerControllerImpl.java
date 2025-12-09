package com.palja.user_service.presentation.controller.impl;

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
import com.palja.user_service.application.command.CreateCustomerCommand;
import com.palja.user_service.application.command.UpdateCustomerCommand;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCustomerDetailRes;
import com.palja.user_service.application.dto.response.ReadCustomerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCustomerDetailRes;
import com.palja.user_service.application.service.CustomerService;
import com.palja.user_service.presentation.controller.CustomerController;
import com.palja.user_service.presentation.dto.request.CreateCustomerReq;
import com.palja.user_service.presentation.dto.request.UpdateCustomerReq;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerControllerImpl implements CustomerController {

	private final CustomerService customerService;

	@Override
	@RequiredAnonymous
	@PostMapping
	public ResponseEntity<ApiResponse<CreateUserRes>> create(@Valid @RequestBody CreateCustomerReq requestDto) {
		CreateCustomerCommand command = CreateCustomerReq.of(requestDto);
		CreateUserRes responseDto = customerService.createCustomer(command);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(responseDto, "일반 사용자가 생성되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@GetMapping
	public ResponseEntity<ApiResponse<PageResponse<ReadCustomerSummaryRes>>> getAll(
		@RequestParam(required = false) String loginId,
		@RequestParam(required = false) String email,
		@RequestParam(required = false) String name,
		Pageable pageable
	) {
		String currentUserLoginId = CurrentUser.getLoginId();

		PageResponse<ReadCustomerSummaryRes> pagedResponseDto = customerService.getAllCustomers(
			currentUserLoginId, loginId, email, name, pageable
		);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(pagedResponseDto, "일반 사용자 목록을 조회했습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@GetMapping("/{loginId}")
	public ResponseEntity<ApiResponse<ReadCustomerDetailRes>> getByLoginId(@PathVariable String loginId) {
		String currentUserLoginId = CurrentUser.getLoginId();

		ReadCustomerDetailRes responseDto = customerService.getCustomerByLoginId(currentUserLoginId, loginId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "일반 사용자를 조회했습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@GetMapping("/internal/{userId}")
	public ResponseEntity<ApiResponse<ReadCustomerDetailRes>> getByUserId(@PathVariable Long userId) {
		String currentUserLoginId = CurrentUser.getLoginId();

		ReadCustomerDetailRes responseDto = customerService.getCustomerByUserId(currentUserLoginId, userId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "일반 사용자를 조회했습니다."));
	}

	@Override
	@RequiredRole({UserRole.CUSTOMER})
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<ReadCustomerDetailRes>> getMe() {
		String currentUserLoginId = CurrentUser.getLoginId();

		ReadCustomerDetailRes responseDto = customerService.getMe(currentUserLoginId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "일반 사용자를 조회했습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@PutMapping("/{loginId}")
	public ResponseEntity<ApiResponse<UpdateCustomerDetailRes>> updateByLoginId(
		@PathVariable String loginId, @Valid @RequestBody UpdateCustomerReq requestDto
	) {
		String currentUserLoginId = CurrentUser.getLoginId();

		UpdateCustomerCommand command = UpdateCustomerReq.of(requestDto);
		UpdateCustomerDetailRes responseDto = customerService.updateCustomerByLoginId(currentUserLoginId, loginId, command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "일반 사용자가 수정되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.CUSTOMER})
	@PutMapping("/me")
	public ResponseEntity<ApiResponse<UpdateCustomerDetailRes>> updateMe(@Valid @RequestBody UpdateCustomerReq requestDto) {
		String currentUserLoginId = CurrentUser.getLoginId();

		UpdateCustomerCommand command = UpdateCustomerReq.of(requestDto);
		UpdateCustomerDetailRes responseDto = customerService.updateMe(currentUserLoginId, command);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(responseDto, "일반 사용자가 수정되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.MANAGER})
	@DeleteMapping("/{loginId}")
	public ResponseEntity<ApiResponse<Void>> deleteByLoginId(@PathVariable String loginId) {
		String currentUserLoginId = CurrentUser.getLoginId();

		customerService.deleteCustomerByLoginId(currentUserLoginId, loginId);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("일반 사용자가 삭제되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.CUSTOMER})
	@DeleteMapping("/me")
	public ResponseEntity<ApiResponse<Void>> deleteMe(
		@RequestHeader(value = "Authorization", required = false) String accessToken, HttpServletResponse response
	) {
		String currentUserLoginId = CurrentUser.getLoginId();

		customerService.deleteMe(accessToken, currentUserLoginId);

		ResponseCookie cookie = ResponseCookie
			.from("refresh_token", "")
			.path("/")
			.httpOnly(true)
			.secure(false)
			.maxAge(0)
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("일반 사용자가 삭제되었습니다."));
	}

}
