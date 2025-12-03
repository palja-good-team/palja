package com.palja.user_service.application.dto.response;

import com.palja.user_service.domain.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class CreateUserRes {

	private String loginId;
	private String name;

	public static CreateUserRes from(User user) {
		return CreateUserRes.builder()
			.loginId(user.getLoginId())
			.name(user.getName())
			.build();
	}

}
