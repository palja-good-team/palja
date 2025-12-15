package com.palja.user_service.application.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.palja.user_service.domain.entity.CompanyUser;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReadCompanyUserDetailRes {

	private Long userId;
	private UUID companyUserId;
	private String loginId;
	private String name;
	private String companyName;
	private String companyNumber;
	private String email;
	private String address;
	private String role;
	private String status;
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;

	public static ReadCompanyUserDetailRes from(CompanyUser companyUser) {
		return ReadCompanyUserDetailRes.builder()
			.userId(companyUser.getUser().getId())
			.companyUserId(companyUser.getId())
			.loginId(companyUser.getUser().getLoginId())
			.name(companyUser.getUser().getName())
			.companyName(companyUser.getCompanyName())
			.companyNumber(companyUser.getCompanyNumber())
			.email(companyUser.getUser().getEmail())
			.address(companyUser.getUser().getAddress())
			.role(companyUser.getUser().getRole().name())
			.status(companyUser.getUser().getStatus().name())
			.createdAt(companyUser.getUser().getCreatedAt())
			.createdBy(companyUser.getUser().getCreatedBy())
			.updatedAt(companyUser.getUser().getUpdatedAt())
			.updatedBy(companyUser.getUser().getUpdatedBy())
			.build();
	}

}
