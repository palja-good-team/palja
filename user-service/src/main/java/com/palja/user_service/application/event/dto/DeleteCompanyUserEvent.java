package com.palja.user_service.application.event.dto;

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
public class DeleteCompanyUserEvent implements UserEvent {

	UUID companyUserId;

	@Override
	public String topic() {
		return "company-user.delete.request";
	}

	public static DeleteCompanyUserEvent from(UUID companyUserId) {
		return DeleteCompanyUserEvent.builder().companyUserId(companyUserId).build();
	}

}
