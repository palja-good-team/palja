package com.palja.user_service.application.service;

public interface AuthService {

	void logout(Long userId, String authHeader);

}
