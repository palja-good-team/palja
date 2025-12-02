package com.palja.user_service.secret;

import java.security.SecureRandom;
import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SecretKeyFactory {

	@Test
	@DisplayName("Create Secret Key")
	void create() {
		byte[] bytes = new byte[64];
		new SecureRandom().nextBytes(bytes);
		String base64Key = Base64.getEncoder().encodeToString(bytes);
		System.out.println(base64Key);
	}

}
