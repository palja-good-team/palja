package com.palja.user_service.infrastructure.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.user_service.infrastructure.security.dto.request.LoginUserReq;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Authentication")
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	public AuthenticationFilter() {
		setFilterProcessesUrl("/api/v1/auth/login");
	}

	@Override
	public Authentication attemptAuthentication(
		HttpServletRequest request, HttpServletResponse response
	) throws AuthenticationException {
		log.info("로그인을 시도합니다.");

		try {
			LoginUserReq requestDto = new ObjectMapper().readValue(request.getInputStream(), LoginUserReq.class);

			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				requestDto.getLoginId(), requestDto.getPassword()
			);

			return getAuthenticationManager().authenticate(token);
		} catch (IOException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	protected void successfulAuthentication(
		HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult
	) throws IOException, ServletException {
		getSuccessHandler().onAuthenticationSuccess(request, response, authResult);
	}

	@Override
	protected void unsuccessfulAuthentication(
		HttpServletRequest request, HttpServletResponse response, AuthenticationException failed
	) throws IOException, ServletException {
		getFailureHandler().onAuthenticationFailure(request, response, failed);
	}

}
