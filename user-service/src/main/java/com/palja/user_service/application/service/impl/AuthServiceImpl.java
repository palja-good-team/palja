package com.palja.user_service.application.service.impl;

import static com.palja.user_service.infrastructure.external.redis.impl.LoginQueueRepositoryImpl.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palja.common.exception.BusinessException;
import com.palja.user_service.application.command.LoginUserCommand;
import com.palja.user_service.application.dto.response.ReadQueueRankRes;
import com.palja.user_service.application.dto.response.TokenRes;
import com.palja.user_service.application.exception.AuthErrorCode;
import com.palja.user_service.application.service.AuthService;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.entity.User;
import com.palja.user_service.domain.repository.LoginQueueRepository;
import com.palja.user_service.domain.repository.TokenRepository;
import com.palja.user_service.domain.repository.UserRepository;
import com.palja.user_service.domain.vo.UserStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final TokenRepository tokenRepository;
	private final LoginQueueRepository loginQueueRepository;

	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;

	@Override
	public String login(LoginUserCommand command) {
		String loginId = command.loginId();
		String password = command.password();

		User user = getUserByLoginId(loginId);
		validateUserPassword(password, user.getPassword());
		validateUserStatus(user);

		loginQueueRepository.enqueueLogin(loginId, System.currentTimeMillis());

		return Base64.getEncoder().encodeToString((UUID.randomUUID() + ":" + loginId).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public TokenRes issueTokens(String queueToken) {
		String loginId = getLoginIdFromQueueToken(queueToken);

		validateQueueTokenWithRedis(loginId);

		User user = getUserByLoginId(loginId);

		String accessToken = jwtUtil.generateAccessToken(user.getLoginId(), user.getRole().name());
		String refreshToken = jwtUtil.generateRefreshToken(user.getLoginId());

		tokenRepository.addRefreshTokenToWhiteList(
			loginId,
			jwtUtil.substringToken(refreshToken),
			jwtUtil.getRefreshKeyExpirationTime()
		);

		return TokenRes.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.refreshKeyExpirationTime(jwtUtil.getRefreshKeyExpirationTime())
			.build();
	}

	@Override
	public String refreshAccessToken(String accessToken, String refreshToken) {
		validateTokenIsNotNull(refreshToken);
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
	public void logout(String currentUserLoginId, String accessToken) {
		validateTokenIsNotNull(accessToken);
		getUserByLoginId(currentUserLoginId);

		addAccessTokenToBlackList(currentUserLoginId, accessToken);
		tokenRepository.deleteRefreshToken(currentUserLoginId);
	}

	@Override
	public ReadQueueRankRes getQueueRank(String queueToken) {
		String loginId = getLoginIdFromQueueToken(queueToken);

		long rank = getQueueRankFromLoginId(loginId);

		if (rank < MAX_CONCURRENT) {
			loginQueueRepository.deleteQueue(loginId);
			loginQueueRepository.addWhiteList(loginId, queueToken);
			rank = 0L;
		} else {
			rank = MAX_CONCURRENT - rank + 1;
		}

		return ReadQueueRankRes.from(loginId, rank);
	}

	private User getUserByLoginId(String loginId) {
		return userRepository.findByLoginIdAndDeletedAtIsNull(loginId).orElseThrow(
			() -> new BusinessException(AuthErrorCode.INVALID_USER_INFO)
		);
	}

	private String getLoginIdFromQueueToken(String queueToken) {
		if (queueToken == null) {
			throw new BusinessException(AuthErrorCode.NOT_FOUND_TOKEN);
		}

		String[] parts = new String(Base64.getDecoder().decode(queueToken), StandardCharsets.UTF_8).split(":");
		if (parts.length != 2) {
			throw new BusinessException(AuthErrorCode.NOT_FOUND_TOKEN);
		}

		return parts[1];
	}

	private Long getQueueRankFromLoginId(String loginId) {
		Long rank = loginQueueRepository.getQueueRank(loginId);
		if (rank == null) {
			throw new BusinessException(AuthErrorCode.NOT_ENQUEUED_USER);
		}

		return rank;
	}

	private void validateUserPassword(String password, String userPassword) {
		if (!passwordEncoder.matches(password, userPassword)) {
			throw new BusinessException(AuthErrorCode.INVALID_USER_INFO);
		}
	}

	private void validateUserStatus(User user) {
		if (user.getStatus().equals(UserStatus.PENDING)) {
			throw new BusinessException(AuthErrorCode.USER_STATUS_PENDING);
		}
	}

	private void validateTokenIsNotNull(String token) {
		if (token == null) {
			throw new BusinessException(AuthErrorCode.NOT_FOUND_TOKEN);
		}
	}

	private void validateRefreshToken(String substringRefreshToken) {
		if (!jwtUtil.validateRefreshToken(substringRefreshToken)) {
			throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	private void validateRefreshTokenWithRedis(String loginId, String refreshToken) {
		String redisRefreshToken = tokenRepository.getRefreshToken(loginId);

		if (!redisRefreshToken.equals(refreshToken)) {
			throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
		}
	}

	private void validateQueueTokenWithRedis(String loginId) {
		if (loginQueueRepository.getQueueToken(loginId) == null) {
			throw new BusinessException(AuthErrorCode.NOT_ALLOWED_TOKEN);
		}
	}

	private void addAccessTokenToBlackList(String loginId, String accessToken) {
		String substringAccessToken = jwtUtil.substringToken(accessToken);
		String hashKey = jwtUtil.hashingTokenToSHA256(substringAccessToken);

		tokenRepository.addAccessTokenToBlackList(
			loginId + ":" + hashKey, substringAccessToken, jwtUtil.getAccessKeyExpirationTime()
		);
	}

}
