package com.palja.user_service.application.util;

import io.jsonwebtoken.Claims;

public interface JwtUtil {

	long getAccessKeyExpirationTime();

	long getRefreshKeyExpirationTime();

	String generateAccessToken(Long userId, String role);

	String generateRefreshToken(Long userId, String role);

	boolean validateAccessToken(String accessToken);

	boolean validateRefreshToken(String refreshToken);

	String substringToken(String token);

	Claims parseAccessToken(String accessToken);

	Claims parseRefreshToken(String refreshToken);

	String hashingTokenToSHA256(String token);

}
