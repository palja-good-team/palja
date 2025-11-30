package com.palja.user_service.infrastructure.security.util;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {

	private final Key accessKey;
	private final long accessKeyExpirationTime;
	private final Key refreshKey;
	@Getter private final long refreshKeyExpirationTime;

	private final String BEARER_PREFIX = "Bearer ";

	public JwtUtil(
		@Value("${jwt.access.secret}") String accessSecret, @Value("${jwt.access.expiration}") Duration accessExpiration,
		@Value("${jwt.refresh.secret}") String refreshSecret, @Value("${jwt.refresh.expiration}") Duration refreshExpiration
	) {
		this.accessKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(accessSecret));
		this.accessKeyExpirationTime = accessExpiration.toMillis();
		this.refreshKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(refreshSecret));
		this.refreshKeyExpirationTime = refreshExpiration.toMillis();
	}

	public String generateAccessToken(Long userId, String role) {
		return generateToken(userId, role, accessKey, accessKeyExpirationTime);
	}

	public String generateRefreshToken(Long userId, String role) {
		return generateToken(userId, role, refreshKey, refreshKeyExpirationTime);
	}

	public boolean validateAccessToken(String accessToken) {
		return validateToken(accessToken, accessKey);
	}

	public boolean validateRefreshToken(String refreshToken) {
		return validateToken(refreshToken, refreshKey);
	}

	public String substringToken(String token) {
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(BEARER_PREFIX.length());
		} else {
			throw new NullPointerException("토큰을 찾을 수 없습니다.");
		}
	}

	public Claims parseAccessToken(String accessToken) {
		return parseToken(accessToken, accessKey);
	}

	public Claims parseRefreshToken(String refreshToken) {
		return parseToken(refreshToken, refreshKey);
	}

	private String generateToken(Long userId, String role, Key key, long expirationTime) {
		Date now = new Date();

		return BEARER_PREFIX +
			Jwts.builder()
				.setSubject(String.valueOf(userId))
				.claim("role", role)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime() + expirationTime))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact()
			;
	}

	private boolean validateToken(String token, Key key) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);
			return true;
		} catch (UnsupportedJwtException e) {
			log.error("지원하지 않는 형식의 토큰입니다.");
		} catch (MalformedJwtException e) {
			log.error("유효하지 않은 형식의 토큰입니다.");
		} catch (SignatureException e) {
			log.error("토큰 서명 검증이 실패했습니다.");
		} catch (SecurityException e) {
			log.error("토큰의 복호화가 실패했습니다.");
		} catch (ExpiredJwtException e) {
			log.error("유효 시간이 만료된 토큰입니다.");
		} catch (IllegalArgumentException e) {
			log.error("값이 비어있는 토큰입니다.");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return false;
	}

	private Claims parseToken(String token, Key key) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}

}
