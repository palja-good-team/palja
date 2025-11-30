package com.palja.user_service.infrastructure.security.handler;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request, HttpServletResponse response, AuthenticationException exception
	) throws IOException {
		String message = (exception instanceof BadCredentialsException)
			? "로그인 정보가 잘못되었습니다."
			: exception.getMessage();

		Map<String, Object> body = Map.of(
			"success", false,
			"code", "UNAUTHORIZED",
			"message", message
		);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
	}

}
