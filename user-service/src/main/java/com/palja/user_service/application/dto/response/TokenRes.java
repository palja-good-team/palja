package com.palja.user_service.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenRes {

	private String accessToken;
	private String refreshToken;
	private long refreshKeyExpirationTime;

}
