package com.palja.user_service.application.dto.response;

import com.palja.user_service.domain.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ReadManagerSummaryRes {

	private String loginId;
	private String name;
	private String email;
	private String status;

	public static ReadManagerSummaryRes from(User user) {
		return ReadManagerSummaryRes.builder()
			.loginId(user.getLoginId())
			.name(user.getName())
			.email(user.getEmail())
			.status(user.getStatus().name())
			.build();
	}

}
