package com.palja.user_service.presentation.util;

import jakarta.servlet.http.HttpServletResponse;

public class HeaderUtil {

	public static void setHeader(HttpServletResponse response, String name, String value) {
		response.setHeader(name, value);
	}

}
