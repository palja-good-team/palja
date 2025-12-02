package com.palja.user_service.application.command;

import lombok.Builder;

@Builder
public record CreateCustomerCommand(

	String loginId,
	String password,
	String name,
	String email,
	String address

) {
}
