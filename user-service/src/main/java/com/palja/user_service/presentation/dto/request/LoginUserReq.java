package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.LoginUserCommand;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginUserReq {

	@NotBlank(message = "아이디를 입력해주세요.")
	private String loginId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	private String password;

	public static LoginUserCommand of(LoginUserReq requestDto) {
		return LoginUserCommand.builder()
			.loginId(requestDto.loginId)
			.password(requestDto.password)
			.build();
	}

}
