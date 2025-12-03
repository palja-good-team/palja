package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.presentation.dto.validation.annotation.ValidCompanyNumber;
import com.palja.user_service.presentation.dto.validation.annotation.ValidEmail;
import com.palja.user_service.presentation.dto.validation.annotation.ValidLoginId;
import com.palja.user_service.presentation.dto.validation.annotation.ValidName;
import com.palja.user_service.presentation.dto.validation.annotation.ValidPassword;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateCompanyUserReq {

	@ValidLoginId
	private String loginId;

	@ValidPassword
	private String password;

	@ValidName
	private String name;

	@NotBlank(message = "업체 이름을 입력해주세요.")
	private String companyName;

	@ValidCompanyNumber
	private String companyNumber;

	@ValidEmail
	private String email;

	@NotBlank(message = "주소를 입력해주세요.")
	private String address;

	public static CreateCompanyUserCommand of(CreateCompanyUserReq requestDto) {
		return CreateCompanyUserCommand.builder()
			.loginId(requestDto.getLoginId())
			.password(requestDto.getPassword())
			.name(requestDto.getName())
			.companyName(requestDto.getCompanyName())
			.companyNumber(requestDto.getCompanyNumber())
			.email(requestDto.getEmail())
			.address(requestDto.getAddress())
			.build();
	}

}
