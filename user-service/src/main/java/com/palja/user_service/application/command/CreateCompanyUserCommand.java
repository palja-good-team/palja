package com.palja.user_service.application.command;

import lombok.Builder;

@Builder
public record CreateCompanyUserCommand(

	String loginId,
	String password,
	String name,
	String companyNumber,
	String email,
	String address

) {
}
