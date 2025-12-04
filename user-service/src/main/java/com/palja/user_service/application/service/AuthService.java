package com.palja.user_service.application.service;

import com.palja.user_service.application.command.LoginUserCommand;
import com.palja.user_service.application.dto.response.TokenRes;

public interface AuthService {

	TokenRes login(LoginUserCommand command);

	String refreshAccessToken(String accessToken, String refreshToken);

	void logout(String loginId, String accessToken);

}
