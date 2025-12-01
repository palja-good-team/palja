package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.CreateCustomerCommand;
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
public class CreateCustomerReq {

	@ValidLoginId
	private String loginId;

	@ValidPassword
	private String password;

	@ValidName
	private String name;

	@ValidEmail
	private String email;

	@NotBlank(message = "주소를 입력해주세요.")
	private String address;

	public static CreateCustomerCommand of(CreateCustomerReq requestDto) {
		return CreateCustomerCommand.builder()
			.loginId(requestDto.getLoginId())
			.password(requestDto.getPassword())
			.name(requestDto.getName())
			.email(requestDto.getEmail())
			.address(requestDto.getAddress())
			.build();
	}

}
