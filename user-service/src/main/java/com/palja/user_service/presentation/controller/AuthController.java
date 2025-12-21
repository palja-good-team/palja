package com.palja.user_service.presentation.controller;

import static com.palja.user_service.presentation.util.AuthResponseExamples.*;

import org.springframework.http.ResponseEntity;

import com.palja.user_service.application.dto.response.ReadQueueRankRes;
import com.palja.user_service.presentation.dto.request.LoginUserReq;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@Tag(name = "Auth-Controller", description = "인증 관련 API")
public interface AuthController {

	@Operation(summary = "로그인", description = "아이디와 비밀번호를 통해 로그인하고 임시 토큰을 발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그인 되었습니다.",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = LOGIN_SUCCESS))),
		@ApiResponse(responseCode = "400", description = "잘못된 요청입니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "잘못된 요청 형식", value = INVALID_BODY),
				@ExampleObject(name = "아이디 미입력", value = EMPTY_LOGIN_ID),
				@ExampleObject(name = "비밀번호 미입력", value = EMPTY_PASSWORD),
			})),
		@ApiResponse(responseCode = "401", description = "인증에 실패했습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "회원 정보 불일치", value = MISMATCH_INFO),
				@ExampleObject(name = "가입 대기 상태", value = STATUS_PENDING),
			})),
		@ApiResponse(responseCode = "403", description = "접근 권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = FORBIDDEN),
			}))
	})
	ResponseEntity<com.palja.common.response.ApiResponse<Void>> login(
		LoginUserReq requestDto, HttpServletResponse response
	);

	@Operation(summary = "토큰 발급", description = "임시 토큰을 통해 액세스 토큰과 리프레시 토큰을 발급합니다.")
	ResponseEntity<com.palja.common.response.ApiResponse<Void>> issue(String queueToken, HttpServletResponse response);

	@Operation(summary = "토큰 재발급", description = "리프레시 토큰을 통해 액세스 토큰을 재발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "토큰이 재발급 되었습니다.",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = REFRESH_SUCCESS))),
		@ApiResponse(responseCode = "401", description = "인증에 실패했습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "잘못된 리프레시 토큰", value = INVALID_REFRESH_TOKEN),
				@ExampleObject(name = "유효하지 않는 회원", value = INVALID_LOGIN_USER),
			}))
	})
	ResponseEntity<com.palja.common.response.ApiResponse<Void>> refresh(
		String accessToken, String refreshToken, HttpServletResponse response
	);

	@Operation(summary = "로그아웃", description = "로그아웃을 하고 액세스 토큰을 블랙리스트에 등록합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그아웃 되었습니다.",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = LOGOUT_SUCCESS))),
		@ApiResponse(responseCode = "401", description = "인증에 실패했습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "유효하지 않는 회원", value = INVALID_LOGIN_USER),
			})),
		@ApiResponse(responseCode = "403", description = "접근 권한이 없습니다.",
			content = @Content(mediaType = "application/json", examples = {
				@ExampleObject(name = "권한 부족", value = FORBIDDEN),
			}))
	})
	ResponseEntity<com.palja.common.response.ApiResponse<Void>> logout(
		String accessToken, HttpServletResponse response
	);

	@Operation(summary = "대기열 확인", description = "자신의 로그인 대기열 순서를 확인합니다.")
	ResponseEntity<com.palja.common.response.ApiResponse<ReadQueueRankRes>> getQueue(String queueToken);

}
