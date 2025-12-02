package com.palja.user_service.presentation.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.palja.common.response.ApiResponse;
import com.palja.user_service.application.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<Void>> refresh(
		@RequestHeader("X-USER-ID") Long userId, @RequestHeader("X-USER-ROLE") String userRole,
		@RequestHeader(value = "Authorization", required = false) String accessToken,
		@CookieValue(value = "refresh_token", required = false) String refreshToken,
		HttpServletResponse response
	) {
		String newAccessToken = authService.refreshAccessToken(userId, userRole, accessToken, refreshToken);
		addAccessTokenToHeader(response, newAccessToken);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("토큰이 재발급 되었습니다."));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@RequestHeader("X-USER-ID") Long userId,
		@RequestHeader("Authorization") String accessToken, HttpServletResponse response
	) {
		authService.logout(userId, accessToken);
		expireRefreshTokenToCookie(response);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("로그아웃 되었습니다."));
	}

	private void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
		response.setHeader("Authorization", accessToken);
	}

	private void expireRefreshTokenToCookie(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie
			.from("refresh_token")
			.maxAge(0)
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

}
