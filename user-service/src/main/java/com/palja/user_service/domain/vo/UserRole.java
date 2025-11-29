package com.palja.user_service.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

	MASTER("ROLE_MASTER"),
	MANAGER("ROLE_MANAGER"),
	CUSTOMER("ROLE_CUSTOMER"),
	COMPANY_USER("ROLE_COMPANY_USER")
	;

	private final String authority;

}
