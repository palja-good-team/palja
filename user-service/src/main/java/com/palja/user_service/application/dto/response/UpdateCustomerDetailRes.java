package com.palja.user_service.application.dto.response;

import java.time.LocalDateTime;

import com.palja.user_service.domain.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class UpdateCustomerDetailRes {

	private Long userId;
	private String loginId;
	private String name;
	private String email;
	private String address;
	private String role;
	private String status;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;

	public static UpdateCustomerDetailRes from(User user) {
		return UpdateCustomerDetailRes.builder()
			.userId(user.getId())
			.loginId(user.getLoginId())
			.name(user.getName())
			.email(user.getEmail())
			.address(user.getAddress())
			.role(user.getRole().name())
			.status(user.getStatus().name())
			.createdAt(user.getCreatedAt())
			.createdBy(user.getCreatedBy())
			.updatedAt(user.getUpdatedAt())
			.updatedBy(user.getUpdatedBy())
			.build();
	}

}
