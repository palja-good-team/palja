package com.palja.user_service.application.command;

import lombok.Builder;

@Builder
public record UpdateCustomerCommand(

	String address

) {
}
