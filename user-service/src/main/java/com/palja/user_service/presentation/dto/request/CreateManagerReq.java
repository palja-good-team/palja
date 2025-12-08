package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.CreateManagerCommand;
import com.palja.user_service.presentation.dto.validation.annotation.ValidEmail;
import com.palja.user_service.presentation.dto.validation.annotation.ValidLoginId;
import com.palja.user_service.presentation.dto.validation.annotation.ValidName;
import com.palja.user_service.presentation.dto.validation.annotation.ValidPassword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateManagerReq {

	@Schema(description = "아이디", example = "manager1")
	@ValidLoginId
	private String loginId;

	@Schema(description = "비밀번호", example = "Password123!")
	@ValidPassword
	private String password;

	@Schema(description = "이름", example = "관리자")
	@ValidName
	private String name;

	@Schema(description = "이메일", example = "manager1@gmail.com")
	@ValidEmail
	private String email;

	@Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
	@NotBlank(message = "주소를 입력해주세요.")
	private String address;

	public static CreateManagerCommand of(CreateManagerReq requestDto) {
		return CreateManagerCommand.builder()
			.loginId(requestDto.getLoginId())
			.password(requestDto.getPassword())
			.name(requestDto.getName())
			.email(requestDto.getEmail())
			.address(requestDto.getAddress())
			.build();
	}

}
