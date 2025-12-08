package com.palja.user_service.presentation.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserDetailRes;
import com.palja.user_service.application.dto.response.ReadCompanyUserSummaryRes;
import com.palja.user_service.application.dto.response.UpdateCompanyUserDetailRes;
import com.palja.user_service.presentation.dto.request.CreateCompanyUserReq;
import com.palja.user_service.presentation.dto.request.UpdateCompanyUserReq;
import com.palja.user_service.presentation.dto.request.UpdateCompanyUserStatusReq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "CompanyUser-Controller", description = "업체 판매자 관련 API")
public interface CompanyUserController {

	@Operation(summary = "업체 판매자 생성", description = "새로운 업체 판매자를 생성합니다.")
	ResponseEntity<ApiResponse<CreateUserRes>> create(CreateCompanyUserReq requestDto);

	@Operation(summary = "업체 판매자 목록 조회", description = "검색 조건에 해당하는 업체 판매자 목록을 페이징하여 조회합니다.")
	ResponseEntity<ApiResponse<PageResponse<ReadCompanyUserSummaryRes>>> getAll(
		String loginId, String email, String name, String status, @ParameterObject Pageable pageable
	);

	@Operation(summary = "업체 판매자 조회 - LoginID", description = "LoginID가 일치하는 업체 판매자의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<ReadCompanyUserDetailRes>> getByLoginId(@Parameter(example = "company1") String loginId);

	@Operation(summary = "업체 판매자 조회 - CompanyUserID", description = "CompanyUserID가 일치하는 업체 판매자의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<ReadCompanyUserDetailRes>> getByCompanyUserId(
		@Parameter(example = "02d2d97b-4cfa-4438-9e8c-6f68709dfbff") UUID companyUserId
	);

	@Operation(summary = "업체 판매자 조회 - 본인", description = "현재 로그인한 업체 판매자 자신의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<ReadCompanyUserDetailRes>> getMe();

	@Operation(summary = "업체 판매자 수정 - LoginID", description = "LoginID가 일치하는 업체 판매자의 정보를 수정합니다.")
	ResponseEntity<ApiResponse<UpdateCompanyUserDetailRes>> updateByLoginId(
		@Parameter(example = "company1") String loginId, UpdateCompanyUserReq requestDto
	);

	@Operation(summary = "업체 판매자 수정 - 본인", description = "현재 로그인한 업체 판매자 자신의 정보를 수정합니다.")
	ResponseEntity<ApiResponse<UpdateCompanyUserDetailRes>> updateMe(UpdateCompanyUserReq requestDto);

	@Operation(summary = "업체 판매자 상태 수정", description = "업체 판매자의 상태를 변경합니다.")
	ResponseEntity<ApiResponse<Void>> updateStatus(
		@Parameter(example = "company1") String loginId, UpdateCompanyUserStatusReq requestDto
	);

	@Operation(summary = "업체 판매자 삭제 - LoginID", description = "LoginID가 일치하는 업체 판매자를 삭제합니다.")
	ResponseEntity<ApiResponse<Void>> deleteByLoginId(@Parameter(example = "company1") String loginId);

	@Operation(summary = "업체 판매자 삭제 - 본인", description = "현재 로그인한 업체 판매자 자신을 삭제합니다.")
	ResponseEntity<ApiResponse<Void>> deleteMe(String accessToken, HttpServletResponse response);

	@Operation(summary = "업체 판매자 삭제 - 가입 거절", description = "회원 가입 요청을 거절하고 업체 판매자를 삭제합니다.")
	ResponseEntity<ApiResponse<Void>> reject(@Parameter(example = "company1") String loginId);

}
