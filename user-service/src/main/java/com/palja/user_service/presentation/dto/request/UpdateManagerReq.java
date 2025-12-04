package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.UpdateManagerCommand;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateManagerReq {

	@NotBlank(message = "주소를 입력해주세요.")
	private String address;

	public static UpdateManagerCommand of(UpdateManagerReq requestDto) {
		return UpdateManagerCommand.builder()
			.address(requestDto.getAddress())
			.build();
	}

}
