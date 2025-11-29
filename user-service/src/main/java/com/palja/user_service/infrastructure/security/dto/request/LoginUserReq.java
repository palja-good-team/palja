package com.palja.user_service.infrastructure.security.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginUserReq {

	private String loginId;
	private String password;

}
