package com.palja.gateway_service.filter;

import static com.palja.gateway_service.util.RedisKeyConstants.*;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.palja.gateway_service.redis.TokenRepository;
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
	private final TokenRepository tokenRepository;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		log.info("[%s] %s".formatted(request.getMethod().name(), request.getURI()));

		List<String> authorizationHeaders = request.getHeaders().get("Authorization");
		if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
			String accessToken = authorizationHeaders.get(0);
			String substringAccessToken = jwtUtil.substringToken(accessToken);

			if (substringAccessToken != null && jwtUtil.validateAccessToken(substringAccessToken)) {
				Claims claims = jwtUtil.parseAccessToken(substringAccessToken);
				String loginId = claims.getSubject();
				String userRole = claims.get("role").toString();

				String hashKey = jwtUtil.hashingTokenToSHA256(substringAccessToken);
				if (tokenRepository.get(ACCESS_TOKEN_BLACKLIST_PREFIX + loginId + ":" + hashKey) == null) {
					ServerHttpRequest mutatedRequest = request.mutate()
						.header("X-USER-LOGIN-ID", loginId)
						.header("X-USER-ROLE", userRole)
						.build();
					return chain.filter(exchange.mutate().request(mutatedRequest).build());
				}
			}
		}

		return chain.filter(exchange);
	}

}