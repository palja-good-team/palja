package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.UpdateCompanyUserCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCompanyUserReq {

	@Schema(description = "업체 이름", example = "업체1")
	private String companyName;

	@Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
	private String address;

	public static UpdateCompanyUserCommand of(UpdateCompanyUserReq requestDto) {
		return UpdateCompanyUserCommand.builder()
			.companyName(requestDto.getCompanyName())
			.address(requestDto.getAddress())
			.build();
	}

}
