package com.palja.user_service.application.dto.response;

import java.time.Instant;

import com.palja.user_service.domain.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ReadManagerDetailRes {

	private Long userId;
	private String loginId;
	private String name;
	private String email;
	private String role;
	private String status;
	private Instant createdAt;
	private String createdBy;
	private Instant updatedAt;
	private String updatedBy;

	public static ReadManagerDetailRes from(User user) {
		return ReadManagerDetailRes.builder()
			.userId(user.getId())
			.loginId(user.getLoginId())
			.name(user.getName())
			.email(user.getEmail())
			.role(user.getRole().name())
			.status(user.getStatus().name())
			.createdAt(user.getCreatedAt())
			.createdBy(user.getCreatedBy())
			.updatedAt(user.getUpdatedAt())
			.updatedBy(user.getUpdatedBy())
			.build();
	}

}
