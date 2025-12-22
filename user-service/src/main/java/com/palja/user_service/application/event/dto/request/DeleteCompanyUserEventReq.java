package com.palja.user_service.application.event.dto.request;

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
public class DeleteCompanyUserEventReq {

	private UUID companyUserId;

	public static DeleteCompanyUserEventReq from(UUID companyUserId) {
		return DeleteCompanyUserEventReq.builder().companyUserId(companyUserId).build();
	}

}
