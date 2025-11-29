package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.presentation.dto.validation.annotation.ValidEmail;
import com.palja.user_service.presentation.dto.validation.annotation.ValidLoginId;
import com.palja.user_service.presentation.dto.validation.annotation.ValidName;
import com.palja.user_service.presentation.dto.validation.annotation.ValidPassword;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateManagerReq {

	@ValidLoginId
	private String loginId;

	@ValidPassword
	private String password;

	@ValidName
	private String name;

	@ValidEmail
	private String email;

	public static CreateManagerCommand of(CreateManagerReq requestDto) {
		return CreateManagerCommand.builder()
			.loginId(requestDto.getLoginId())
			.password(requestDto.getPassword())
			.name(requestDto.getName())
			.email(requestDto.getEmail())
			.build();
	}

}
