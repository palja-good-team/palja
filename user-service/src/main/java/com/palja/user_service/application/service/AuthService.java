package com.palja.user_service.application.service;

public interface AuthService {

	String refreshAccessToken(String loginId, String userRole, String accessToken, String refreshToken);

	void logout(String loginId, String accessToken);

}
