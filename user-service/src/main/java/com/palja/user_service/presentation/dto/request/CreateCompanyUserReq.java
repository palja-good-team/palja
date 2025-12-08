package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.CreateCompanyUserCommand;
import com.palja.user_service.presentation.dto.validation.annotation.ValidCompanyNumber;
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
public class CreateCompanyUserReq {

	@Schema(description = "아이디", example = "company1")
	@ValidLoginId
	private String loginId;

	@Schema(description = "비밀번호", example = "Password123!")
	@ValidPassword
	private String password;

	@Schema(description = "이름", example = "판매자")
	@ValidName
	private String name;

	@Schema(description = "업체 이름", example = "업체1")
	@NotBlank(message = "업체 이름을 입력해주세요.")
	private String companyName;

	@Schema(description = "사업자 등록 번호", example = "123-45-67890")
	@ValidCompanyNumber
	private String companyNumber;

	@Schema(description = "이메일", example = "company1@gmail.com")
	@ValidEmail
	private String email;

	@Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
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
