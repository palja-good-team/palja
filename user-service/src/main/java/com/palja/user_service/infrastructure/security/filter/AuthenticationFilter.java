package com.palja.user_service.infrastructure.security.filter;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.user_service.infrastructure.security.dto.request.LoginUserReq;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Authentication")
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	public AuthenticationFilter() {
		setFilterProcessesUrl("/api/v1/login");
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
	) throws IOException {
		log.info("로그인이 성공했습니다.");

		Map<String, Object> body = Map.of(
			"success", true,
			"code", "OK",
			"message", "로그인이 성공했습니다."
		);

		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
	}

	@Override
	protected void unsuccessfulAuthentication(
		HttpServletRequest request, HttpServletResponse response, AuthenticationException failed
	) throws IOException {
		log.info(failed.getMessage());

		Map<String, Object> body = Map.of(
			"success", false,
			"code", "UNAUTHORIZED",
			"message", failed.getMessage()
		);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
	}

}
