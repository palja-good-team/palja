package com.palja.user_service.presentation.controller.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.palja.common.annotation.RequiredAnonymous;
import com.palja.common.annotation.RequiredRole;
import com.palja.common.auditor.CurrentUser;
import com.palja.common.response.ApiResponse;
import com.palja.common.vo.UserRole;
import com.palja.user_service.application.command.LoginUserCommand;
import com.palja.user_service.application.dto.response.TokenRes;
import com.palja.user_service.application.service.AuthService;
import com.palja.user_service.presentation.controller.AuthController;
import com.palja.user_service.presentation.dto.request.LoginUserReq;
import com.palja.user_service.presentation.util.CookieUtil;
import com.palja.user_service.presentation.util.HeaderUtil;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

	private final AuthService authService;

	@Override
	@RequiredAnonymous
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody LoginUserReq requestDto, HttpServletResponse response) {
		LoginUserCommand command = LoginUserReq.of(requestDto);
		String authToken = authService.login(command);

		HeaderUtil.setHeader(response, "X-AUTH-TOKEN", authToken);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("로그인 되었습니다."));
	}

	@Override
	@RequiredAnonymous
	@PostMapping("/tokens")
	public ResponseEntity<ApiResponse<Void>> issue(
		@RequestHeader(value = "X-AUTH-TOKEN", required = false) String authToken, HttpServletResponse response
	) {
		TokenRes tokens = authService.issueTokens(authToken);

		String accessToken = tokens.getAccessToken();
		HeaderUtil.setHeader(response, "Authorization", accessToken);

		String refreshToken = tokens.getRefreshToken();
		String encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8).replace("\\+", "%20");
		long refreshKeyExpirationTime = tokens.getRefreshKeyExpirationTime();
		CookieUtil.addCookieToHeader(response, "refresh_token", encodedRefreshToken, refreshKeyExpirationTime);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("토큰이 발급 되었습니다."));
	}

	@Override
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<Void>> refresh(
		@RequestHeader(value = "Authorization", required = false) String accessToken,
		@CookieValue(value = "refresh_token", required = false) String refreshToken,
		HttpServletResponse response
	) {
		String newAccessToken = authService.refreshAccessToken(accessToken, refreshToken);
		HeaderUtil.setHeader(response, "Authorization", newAccessToken);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("토큰이 재발급 되었습니다."));
	}

	@Override
	@RequiredRole({UserRole.MASTER, UserRole.MANAGER, UserRole.CUSTOMER, UserRole.COMPANY_USER})
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@RequestHeader(value = "Authorization", required = false) String accessToken, HttpServletResponse response
	) {
		authService.logout(CurrentUser.getLoginId(), accessToken);
		CookieUtil.addCookieToHeader(response, "refresh_token", "", 0);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("로그아웃 되었습니다."));
	}

}
