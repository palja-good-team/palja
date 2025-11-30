package com.palja.user_service.presentation.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@RequestHeader("X-USER-ID") Long userId,
		HttpServletResponse response, @RequestHeader("Authorization") String token
	) {
		authService.logout(userId, token);
		expireRefreshTokenToCookie(response);
		System.out.println(userId);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("로그아웃 되었습니다."));
	}

	private void expireRefreshTokenToCookie(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie
			.from("refreshToken")
			.maxAge(0)
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

}
