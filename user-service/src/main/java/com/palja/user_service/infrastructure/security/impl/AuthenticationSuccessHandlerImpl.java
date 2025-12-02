package com.palja.user_service.infrastructure.security.impl;

import static com.palja.user_service.application.util.RedisKeyConstants.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.repository.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;
	private final TokenRepository tokenRepository;


	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request, HttpServletResponse response, Authentication authentication
	) throws IOException {
		String loginId = ((UserDetailsImpl)authentication.getPrincipal()).getUsername();
		String userRole = ((UserDetailsImpl)authentication.getPrincipal()).getUserRole();

		String accessToken = jwtUtil.generateAccessToken(loginId, userRole);
		addAccessTokenToHeader(response, accessToken);

		String refreshToken = jwtUtil.generateRefreshToken(loginId);
		addRefreshTokenToCookie(response, refreshToken);
		String substringRefreshToken = jwtUtil.substringToken(refreshToken);
		tokenRepository.save(REFRESH_TOKEN_WHITELIST_PREFIX + loginId, substringRefreshToken, jwtUtil.getRefreshKeyExpirationTime());

		setResponse(response);

		log.info("로그인이 성공했습니다.");
	}

	private void addAccessTokenToHeader(HttpServletResponse response, String accessToken) {
		response.setHeader("Authorization", accessToken);
	}

	private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
		refreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
		ResponseCookie cookie = ResponseCookie
			.from("refresh_token", refreshToken)
			.path("/")
			.httpOnly(true)
			.secure(false) // HTTPS
			.maxAge(jwtUtil.getRefreshKeyExpirationTime())
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	private void setResponse(HttpServletResponse response) throws IOException {
		Map<String, Object> body = Map.of(
			"success", true,
			"code", "OK",
			"message", "로그인이 성공했습니다."
		);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
	}

}
