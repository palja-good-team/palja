package com.palja.user_service.application.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RedisKeyConstants {

	public static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "AUTH:BL:AT:";
	public static final String REFRESH_TOKEN_WHITELIST_PREFIX = "AUTH:WL:RT:";

}
