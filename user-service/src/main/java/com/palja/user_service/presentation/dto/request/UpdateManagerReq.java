package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.UpdateManagerCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateManagerReq {

	@Schema(description = "주소", example = "서울시 강남구 테헤란로 123")
	private String address;

	public static UpdateManagerCommand of(UpdateManagerReq requestDto) {
		return UpdateManagerCommand.builder()
			.address(requestDto.getAddress())
			.build();
	}

}
