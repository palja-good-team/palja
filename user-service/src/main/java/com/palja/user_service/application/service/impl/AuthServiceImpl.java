package com.palja.user_service.application.service.impl;

import static com.palja.user_service.application.util.RedisKeyConstants.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.palja.user_service.application.service.AuthService;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.repository.TokenRepository;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;

	private final JwtUtil jwtUtil;

	@Override
	public String refreshAccessToken(Long userId, String userRole, String accessToken, String refreshToken) {
		String substringRefreshToken = jwtUtil.substringToken(URLDecoder.decode(refreshToken, StandardCharsets.UTF_8));
		validateRefreshToken(substringRefreshToken);

		if (accessToken != null) {
			addAccessTokenToBlackList(userId, accessToken);
		}

		return jwtUtil.generateAccessToken(userId, userRole);
	}

	@Override
	public void logout(Long userId, String accessToken) {
		addAccessTokenToBlackList(userId, accessToken);
		tokenRepository.remove(REFRESH_TOKEN_WHITELIST_PREFIX + userId);
	}

	private void validateRefreshToken(String refreshToken) {
		if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
			throw new IllegalArgumentException("다시 로그인 해주세요.");
		}
	}

	private void addAccessTokenToBlackList(Long userId, String accessToken) {
		String substringAccessToken = jwtUtil.substringToken(accessToken);
		String hashKey = jwtUtil.hashingTokenToSHA256(substringAccessToken);

		tokenRepository.save(
			ACCESS_TOKEN_BLACKLIST_PREFIX + userId + ":" + hashKey, substringAccessToken, jwtUtil.getAccessKeyExpirationTime()
		);
	}

}
