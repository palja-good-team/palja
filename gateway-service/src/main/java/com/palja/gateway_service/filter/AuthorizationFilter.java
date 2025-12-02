package com.palja.gateway_service.filter;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palja.gateway_service.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationFilter implements GlobalFilter {

	private final JwtUtil jwtUtil;
	private final ObjectMapper objectMapper;

	private final Map<String, List<String>> permitAllPaths = Map.of(
		"/api/v1/auth/login", List.of("POST"),
		"/api/v1/auth/refresh", List.of("POST")
	);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();
		String method = request.getMethod().name();

		log.info("[%s] %s".formatted(method, request.getURI()));

		if (permitAllPaths.containsKey(path) && permitAllPaths.get(path).contains(method)) {
			return chain.filter(exchange);
		}

		List<String> authorizationHeaders = request.getHeaders().get("Authorization");

		if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
			String accessToken = authorizationHeaders.get(0);
			String substringAccessToken = jwtUtil.substringToken(accessToken);

			if (substringAccessToken != null && jwtUtil.validateAccessToken(substringAccessToken)) {
				Claims claims = jwtUtil.parseAccessToken(substringAccessToken);
				String loginId = claims.getSubject();
				String userRole = claims.get("role").toString();

				ServerHttpRequest mutatedRequest = request.mutate()
					.header("X-USER-LOGIN-ID", loginId)
					.header("X-USER-ROLE", userRole)
					.build();

				return chain.filter(exchange.mutate().request(mutatedRequest).build());
			}
		}

		exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
		exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

		return exchange.getResponse().writeWith(getBuffer(exchange));
	}

	private Mono<DataBuffer> getBuffer(ServerWebExchange exchange) {
		try {
			byte[] bytes = objectMapper.writeValueAsBytes(getBody());
			DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
			return Mono.just(buffer);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, Object> getBody() {
		return Map.of(
			"success", false,
			"code", "FORBIDDEN",
			"message", "접근 권한이 없습니다."
		);
	}

}
