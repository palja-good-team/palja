package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.UpdateCompanyUserStatusCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCompanyUserStatusReq {

	@Schema(description = "상태", example = "active")
	@NotBlank(message = "상태를 입력해주세요.")
	private String status;

	public static UpdateCompanyUserStatusCommand of(UpdateCompanyUserStatusReq requestDto) {
		return UpdateCompanyUserStatusCommand.builder()
			.status(requestDto.getStatus())
			.build();
	}

}
