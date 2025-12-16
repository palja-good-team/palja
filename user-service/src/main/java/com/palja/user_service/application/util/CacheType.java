package com.palja.user_service.application.util;

import static com.palja.user_service.application.util.RedisKeyConstants.*;

import com.palja.user_service.application.dto.response.ReadCompanyUserDetailRes;
import com.palja.user_service.application.dto.response.ReadCustomerDetailRes;
import com.palja.user_service.application.dto.response.ReadManagerDetailRes;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum CacheType {

	MANAGER(MANAGER_CACHE_PREFIX, ReadManagerDetailRes.class),
	CUSTOMER(CUSTOMER_CACHE_PREFIX, ReadCustomerDetailRes.class),
	COMPANY_USER(COMPANY_USER_CACHE_PREFIX, ReadCompanyUserDetailRes.class),
	;

	private final String cacheName;
	private final Class<?> valueType;

}
