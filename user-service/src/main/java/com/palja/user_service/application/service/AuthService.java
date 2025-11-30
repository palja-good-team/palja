package com.palja.user_service.application.service;

public interface AuthService {

	String refreshAccessToken(Long userId, String userRole, String accessToken, String refreshToken);

	void logout(Long userId, String accessToken);

}
