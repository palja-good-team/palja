package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.UpdateCustomerCommand;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCustomerReq {

	@NotBlank(message = "주소를 입력해주세요.")
	private String address;

	public static UpdateCustomerCommand of(UpdateCustomerReq requestDto) {
		return UpdateCustomerCommand.builder()
			.address(requestDto.getAddress())
			.build();
	}

}
