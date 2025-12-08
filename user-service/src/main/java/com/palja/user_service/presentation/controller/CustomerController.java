package com.palja.user_service.presentation.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCustomerDetailRes;
import com.palja.user_service.application.dto.response.ReadCustomerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCustomerDetailRes;
import com.palja.user_service.presentation.dto.request.CreateCustomerReq;
import com.palja.user_service.presentation.dto.request.UpdateCustomerReq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Customer-Controller", description = "일반 사용자 관련 API")
public interface CustomerController {

	@Operation(summary = "일반 사용자 생성", description = "새로운 일반 사용자를 생성합니다.")
	ResponseEntity<ApiResponse<CreateUserRes>> create(CreateCustomerReq requestDto);

	@Operation(summary = "일반 사용자 목록 조회", description = "검색 조건에 해당하는 일반 사용자 목록을 페이징하여 조회합니다.")
	ResponseEntity<ApiResponse<PageResponse<ReadCustomerSummaryRes>>> getAll(
		String loginId, String email, String name, @ParameterObject Pageable pageable
	);

	@Operation(summary = "일반 사용자 조회 - LoginID", description = "LoginID가 일치하는 일반 사용자의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<ReadCustomerDetailRes>> getByLoginId(@Parameter(example = "customer1") String loginId);

	@Operation(summary = "일반 사용자 조회 - UserID", description = "UserID가 일치하는 일반 사용자의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<ReadCustomerDetailRes>> getByUserId(@Parameter(example = "1") Long userId);

	@Operation(summary = "일반 사용자 조회 - 본인", description = "현재 로그인한 일반 사용자 자신의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<ReadCustomerDetailRes>> getMe();

	@Operation(summary = "일반 사용자 수정 - LoginID", description = "LoginID가 일치하는 일반 사용자의 정보를 수정합니다.")
	ResponseEntity<ApiResponse<UpdateCustomerDetailRes>> updateByLoginId(
		@Parameter(example = "customer1") String loginId, UpdateCustomerReq requestDto
	);

	@Operation(summary = "일반 사용자 수정 - 본인", description = "현재 로그인한 일반 사용자 자신의 정보를 수정합니다.")
	ResponseEntity<ApiResponse<UpdateCustomerDetailRes>> updateMe(UpdateCustomerReq requestDto);

	@Operation(summary = "일반 사용자 삭제 - LoginID", description = "LoginID가 일치하는 일반 사용자를 삭제합니다.")
	ResponseEntity<ApiResponse<Void>> deleteByLoginId(@Parameter(example = "customer1") String loginId);

	@Operation(summary = "일반 사용자 삭제 - 본인", description = "현재 로그인한 일반 사용자 자신을 삭제합니다.")
	ResponseEntity<ApiResponse<Void>> deleteMe(String accessToken, HttpServletResponse response);

}
