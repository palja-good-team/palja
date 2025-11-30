package com.palja.user_service.application.service.impl;

import static com.palja.user_service.application.util.RedisKeyConstants.*;

import org.springframework.stereotype.Service;

import com.palja.user_service.application.service.AuthService;
import com.palja.user_service.application.util.JwtUtil;
import com.palja.user_service.domain.external.redis.RedisRepository;
import com.palja.user_service.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RedisRepository redisRepository;

	private final JwtUtil jwtUtil;

	@Override
	public void logout(String token) {
		String accessToken = jwtUtil.substringToken(token);
		String userId = jwtUtil.parseAccessToken(accessToken).getSubject();
		String hashKey = jwtUtil.hashingTokenToSHA256(accessToken);

		redisRepository.save(
			ACCESS_TOKEN_BLACKLIST_PREFIX + userId + ":" + hashKey, accessToken, jwtUtil.getAccessKeyExpirationTime()
		);
		redisRepository.remove(REFRESH_TOKEN_WHITELIST_PREFIX + userId);
	}

}
