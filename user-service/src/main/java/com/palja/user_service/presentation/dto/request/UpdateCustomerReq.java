package com.palja.user_service.presentation.dto.request;

import com.palja.user_service.application.command.UpdateCustomerCommand;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateCustomerReq {

	private String address;

	public static UpdateCustomerCommand of(UpdateCustomerReq requestDto) {
		return UpdateCustomerCommand.builder()
			.address(requestDto.getAddress())
			.build();
	}

}
