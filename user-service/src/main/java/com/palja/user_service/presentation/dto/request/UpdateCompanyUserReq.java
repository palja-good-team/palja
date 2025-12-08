package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.UpdateCompanyUserCommand;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCompanyUserReq {

	private String companyName;
	private String address;

	public static UpdateCompanyUserCommand of(UpdateCompanyUserReq requestDto) {
		return UpdateCompanyUserCommand.builder()
			.companyName(requestDto.getCompanyName())
			.address(requestDto.getAddress())
			.build();
	}

}
