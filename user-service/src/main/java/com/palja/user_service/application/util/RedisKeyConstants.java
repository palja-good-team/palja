package com.palja.user_service.application.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisKeyConstants {

	/// Token Key
	public static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "AUTH:BL:AT:";
	public static final String REFRESH_TOKEN_WHITELIST_PREFIX = "AUTH:WL:RT:";

	/// Cache Key
	public static final String MANAGER_CACHE_PREFIX = "user:manager";
	public static final String CUSTOMER_CACHE_PREFIX = "user:customer";
	public static final String COMPANY_USER_CACHE_PREFIX = "user:companyUser";

}
