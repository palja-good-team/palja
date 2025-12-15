package com.palja.user_service.infrastructure.util;

import com.palja.user_service.application.dto.response.ReadCompanyUserDetailRes;
import com.palja.user_service.application.dto.response.ReadCustomerDetailRes;
import com.palja.user_service.application.dto.response.ReadManagerDetailRes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CacheType {

	MANAGER("user:manager", ReadManagerDetailRes.class),
	CUSTOMER("user:customer", ReadCustomerDetailRes.class),
	COMPANY_USER("user:company_user", ReadCompanyUserDetailRes.class),
	;

	private final String cacheName;
	private final Class<?> valueType;

}
