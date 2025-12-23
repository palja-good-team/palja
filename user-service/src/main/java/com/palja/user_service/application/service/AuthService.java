package com.palja.user_service.application.service;

import com.palja.user_service.application.command.LoginUserCommand;
import com.palja.user_service.application.dto.response.ReadQueueRankRes;
import com.palja.user_service.application.dto.response.TokenRes;

public interface AuthService {

	String login(LoginUserCommand command);

	TokenRes issueTokens(String queueToken);

	String refreshAccessToken(String accessToken, String refreshToken);

	void logout(String currentUserLoginId, String accessToken);

	ReadQueueRankRes getQueueRank(String queueToken);

}
