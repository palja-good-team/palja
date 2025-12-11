package com.palja.user_service.presentation.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

	public static void addCookieToHeader(HttpServletResponse response, String name, String value, long maxAge) {
		ResponseCookie responseCookie = setCookie(name, value, maxAge);
		response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
	}

	private static ResponseCookie setCookie(String name, String value, long maxAge) {
		return ResponseCookie
			.from(name, value)
			.path("/")
			.httpOnly(true)
			.secure(false)
			.maxAge(maxAge)
			.build();
	}

}
