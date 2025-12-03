package com.palja.user_service.presentation.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.palja.common.auditor.AuditorContext;
import com.palja.common.response.ApiResponse;
import com.palja.user_service.application.command.LoginUserCommand;
import com.palja.user_service.application.dto.response.TokenRes;
import com.palja.user_service.application.service.AuthService;
import com.palja.user_service.presentation.dto.request.LoginUserReq;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody LoginUserReq requestDto, HttpServletResponse response) {
		LoginUserCommand command = LoginUserReq.of(requestDto);
		TokenRes tokenResponse = authService.login(command);

		String accessToken = tokenResponse.getAccessToken();
		addAccessTokenToHeader(response, accessToken);

		String refreshToken = tokenResponse.getRefreshToken();
		String encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8).replace("\\+", "%20");
		long refreshKeyExpirationTime = tokenResponse.getRefreshKeyExpirationTime();
		addRefreshTokenToCookie(response, encodedRefreshToken, refreshKeyExpirationTime);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("로그인 되었습니다."));
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<Void>> refresh(
		@RequestHeader(value = "Authorization", required = false) String accessToken,
		@CookieValue(value = "refresh_token", required = false) String refreshToken,
		HttpServletResponse response
	) {
		String newAccessToken = authService.refreshAccessToken(accessToken, refreshToken);
		addAccessTokenToHeader(response, newAccessToken);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("토큰이 재발급 되었습니다."));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(
		@RequestHeader("Authorization") String accessToken, HttpServletResponse response
	) {
		String loginId = AuditorContext.get().getLoginId();

		authService.logout(loginId, accessToken);
		addRefreshTokenToCookie(response, "", 0);

		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("로그아웃 되었습니다."));
	}

	private void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
		response.setHeader("Authorization", accessToken);
	}

	private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken, long maxAge) {
		ResponseCookie cookie = ResponseCookie
			.from("refresh_token", refreshToken)
			.path("/")
			.httpOnly(true)
			.secure(false)
			.maxAge(maxAge)
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

}
