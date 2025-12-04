package com.palja.user_service.application.command;

import lombok.Builder;

@Builder
public record LoginUserCommand(

	String loginId,
	String password

) {
}
