package com.palja.user_service.presentation.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.palja.common.response.ApiResponse;
import com.palja.common.response.PageResponse;
import com.palja.user_service.application.dto.response.CreateUserRes;
import com.palja.user_service.application.dto.response.ReadManagerDetailRes;
import com.palja.user_service.application.dto.response.ReadManagerSummaryRes;
import com.palja.user_service.application.dto.response.UpdateManagerDetailRes;
import com.palja.user_service.presentation.dto.request.CreateManagerReq;
import com.palja.user_service.presentation.dto.request.UpdateManagerReq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Manager-Controller", description = "관리자 관련 API")
public interface ManagerController {

	@Operation(summary = "관리자 생성", description = "새로운 관리자를 생성합니다.")
	ResponseEntity<ApiResponse<CreateUserRes>> create(CreateManagerReq requestDto);

	@Operation(summary = "관리자 목록 조회", description = "검색 조건에 해당하는 관리자 목록을 페이징하여 조회합니다.")
	ResponseEntity<ApiResponse<PageResponse<ReadManagerSummaryRes>>> getAll(
		String loginId, String email, String name, @ParameterObject Pageable pageable
	);

	@Operation(summary = "관리자 조회 - LoginID", description = "LoginID가 일치하는 관리자의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<ReadManagerDetailRes>> getByLoginId(@Parameter(example = "manager1") String loginId);

	@Operation(summary = "관리자 조회 - UserID", description = "UserID가 일치하는 관리자의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<ReadManagerDetailRes>> getByUserId(@Parameter(example = "1") Long userId);

	@Operation(summary = "관리자 조회 - 본인", description = "현재 로그인한 관리자 자신의 상세 정보를 조회합니다.")
	ResponseEntity<ApiResponse<ReadManagerDetailRes>> getMe();

	@Operation(summary = "관리자 수정 - LoginID", description = "LoginID가 일치하는 관리자의 정보를 수정합니다.")
	ResponseEntity<ApiResponse<UpdateManagerDetailRes>> updateByLoginId(
		@Parameter(example = "manager1") String loginId, UpdateManagerReq requestDto
	);

	@Operation(summary = "관리자 수정 - 본인", description = "현재 로그인한 관리자 자신의 정보를 수정합니다.")
	ResponseEntity<ApiResponse<UpdateManagerDetailRes>> updateMe(UpdateManagerReq requestDto);

	@Operation(summary = "관리자 삭제 - LoginID", description = "LoginID가 일치하는 관리자를 삭제합니다.")
	ResponseEntity<ApiResponse<Void>> deleteByLoginId(@Parameter(example = "manager1") String loginId);

}
