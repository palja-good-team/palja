package com.palja.user_service.application.service.impl;

import static com.palja.user_service.application.util.RedisKeyConstants.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import com.palja.common.exception.BusinessException;
import com.palja.common.exception.CommonErrorCode;
import com.palja.user_service.application.service.AuthService;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.entity.User;
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
	public String refreshAccessToken(String accessToken, String refreshToken) {
		String substringRefreshToken = jwtUtil.substringToken(URLDecoder.decode(refreshToken, StandardCharsets.UTF_8));
		validateRefreshToken(substringRefreshToken);
		String loginId = jwtUtil.parseRefreshToken(substringRefreshToken).getSubject();

		validateRefreshTokenWithRedis(loginId, substringRefreshToken);
		String userRole = getUserByLoginId(loginId).getRole().name();

		if (accessToken != null) {
			addAccessTokenToBlackList(loginId, accessToken);
		}

		return jwtUtil.generateAccessToken(loginId, userRole);
	}

	@Override
	public void logout(String loginId, String accessToken) {
		getUserByLoginId(loginId);

		addAccessTokenToBlackList(loginId, accessToken);
		tokenRepository.remove(REFRESH_TOKEN_WHITELIST_PREFIX + loginId);
	}

	private User getUserByLoginId(String loginId) {
		return userRepository.findByLoginIdAndDeletedAtIsNull(loginId).orElseThrow(
			() -> new BusinessException(CommonErrorCode.NOT_FOUND)
		);
	}

	private void validateRefreshToken(String refreshToken) {
		if (refreshToken == null || !jwtUtil.validateRefreshToken(refreshToken)) {
			throw new IllegalArgumentException("다시 로그인 해주세요.");
		}
	}

	private void validateRefreshTokenWithRedis(String loginId, String refreshToken) {
		String redisRefreshToken = tokenRepository.get(REFRESH_TOKEN_WHITELIST_PREFIX + loginId);

		if (!redisRefreshToken.equals(refreshToken)) {
			throw new IllegalArgumentException("다시 로그인 해주세요.");
		}
	}

	private void addAccessTokenToBlackList(String loginId, String accessToken) {
		String substringAccessToken = jwtUtil.substringToken(accessToken);
		String hashKey = jwtUtil.hashingTokenToSHA256(substringAccessToken);

		tokenRepository.save(
			ACCESS_TOKEN_BLACKLIST_PREFIX + loginId + ":" + hashKey, substringAccessToken, jwtUtil.getAccessKeyExpirationTime()
		);
	}

}
