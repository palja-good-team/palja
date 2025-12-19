package com.palja.user_service.application.event.dto.impl;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeleteCompanyUserEvent {

	private UUID companyUserId;

	public static DeleteCompanyUserEvent from(UUID companyUserId) {
		return DeleteCompanyUserEvent.builder().companyUserId(companyUserId).build();
	}

}
